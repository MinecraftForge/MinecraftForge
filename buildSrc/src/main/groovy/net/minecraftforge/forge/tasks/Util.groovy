package net.minecraftforge.forge.tasks

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

final class Util {
    public static final int ASM_LEVEL = Opcodes.ASM9
    private static final HttpClient HTTP = HttpClient.newBuilder().build()

    static void init() {
        File.metaClass.sha1 = { ->
            MessageDigest md = MessageDigest.getInstance('SHA-1')
            delegate.eachByte(4096) { byte[] bytes, int size ->
                md.update(bytes, 0, size)
            }
            return md.digest().collect(this.&toHex).join('')
        }
        File.metaClass.getSha1 = { !delegate.exists() ? null : delegate.sha1() }
        File.metaClass.sha256 = { ->
            MessageDigest md = MessageDigest.getInstance('SHA-256')
            delegate.eachByte(4096) { byte[] bytes, int size ->
                md.update(bytes, 0, size)
            }
            return md.digest().collect(this.&toHex).join('')
        }
        File.metaClass.getSha256 = { !delegate.exists() ? null : delegate.sha256() }

        File.metaClass.json = { -> new JsonSlurper().parseText(delegate.text) }
        File.metaClass.getJson = { return delegate.exists() ? new JsonSlurper().parse(delegate) : [:] }
        File.metaClass.setJson = { json -> delegate.text = new JsonBuilder(json).toPrettyString() }

        Date.metaClass.iso8601 = { ->
            var format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            var result = format.format(delegate)
            return result[0..21] + ':' + result[22..-1]
        }

        String.metaClass.rsplit = { String del, int limit = -1 ->
            var lst = new ArrayList<String>()
            int x = 0
            int idx
            String tmp = delegate
            while ((idx = tmp.lastIndexOf(del)) != -1 && (limit === -1 || x++ < limit)) {
                lst.add(0, tmp.substring(idx + del.length(), tmp.length()))
                tmp = tmp.substring(0, idx)
            }
            lst.add(0, tmp)
            return lst
        }
    }

    static String[] getClasspath(Project project, Map libs, String artifact) {
        def ret = []
        artifactTree(project, artifact).each { key, lib ->
            libs[lib.name] = lib
            if (lib.name != artifact)
                ret.add(lib.name)
        }
        return ret
    }

    @CompileStatic
    static Map getArtifacts(Project project, Configuration config) {
        var ret = new ConcurrentHashMap()
        config.resolvedConfiguration.resolvedArtifacts.parallelStream().forEach(dep -> {
            var info = getMavenInfoFromDep(dep)
            var url = "https://libraries.minecraft.net/$info.path"
            if (!checkExists(url))
                url = "https://maven.minecraftforge.net/$info.path"

            ret[info.key] = [
                name: info.name,
                downloads: [
                    artifact: [
                        path: info.path,
                        url: url.toString(),
                        sha1: sha1(dep.file),
                        size: dep.file.length()
                    ]
                ]
            ]
        })
        return ret
    }

    @CompileStatic
    static Map getMavenInfoFromDep(ResolvedArtifact dep) {
        return getMavenInfoFromMap([
            group: dep.moduleVersion.id.group,
            name: dep.moduleVersion.id.name,
            version: dep.moduleVersion.id.version,
            classifier: dep.classifier,
            extension: dep.extension
        ])
    }

    @CompileStatic
    static Map getMavenInfoFromTask(AbstractArchiveTask task) {
        return getMavenInfoFromMap([
            group: task.project.group.toString(),
            name: task.project.name,
            version: task.project.version.toString(),
            classifier: task.archiveClassifier.get(),
            extension: task.archiveExtension.get()
        ])
    }

    @CompileStatic
    static Map getMavenInfoFromTask(Task task, String classifier) {
        return getMavenInfoFromMap([
            group: task.project.group.toString(),
            name: task.project.name,
            version: task.project.version.toString(),
            classifier: classifier,
            extension: 'jar'
        ])
    }

    @CompileStatic
    private static Map getMavenInfoFromMap(Map<String, String> art) {
        var key = "$art.group:$art.name"
        var name = "$art.group:$art.name:$art.version"
        var path = "${art.group.replace('.', '/')}/$art.name/$art.version/$art.name-$art.version"
        if (art.classifier !== null) {
            name += ":$art.classifier"
            path += "-$art.classifier"
        }
        if ('jar' != art.extension) {
            name += "@$art.extension"
            path += ".$art.extension"
        } else {
            path += ".jar"
        }
        return [
            key: key.toString(),
            name: name.toString(),
            path: path.toString(),
            art: art
        ]
    }

    static String iso8601Now() { new Date().iso8601() }

    @CompileStatic
    static String sha1(File file) {
        MessageDigest md = MessageDigest.getInstance('SHA-1')
        file.eachByte(4096) { byte[] bytes, int size ->
            md.update(bytes, 0, size)
        }
        return md.digest().collect(this.&toHex).join('')
    }

    @CompileStatic
    private static String toHex(byte bite) {
        return String.format('%02x', bite)
    }

    private static Map artifactTree(Project project, String artifact, boolean transitive = true) {
        if (!project.ext.has('tree_resolver'))
            project.ext.tree_resolver = 1
        def cfg = project.configurations.create('tree_resolver_' + project.ext.tree_resolver++)
        cfg.transitive = transitive
        def dep = project.dependencies.create(artifact)
        cfg.dependencies.add(dep)
        def files = cfg.resolve()
        return getArtifacts(project, cfg)
    }

    @CompileStatic
    static boolean checkExists(String url) {
        try {
            return HTTP.send(HttpRequest.newBuilder(new URI(url))
                    .method('HEAD', HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.discarding()
            ).statusCode() === 200
        } catch (Exception e) {
            if (e.toString().contains('unable to find valid certification path to requested target'))
                throw new RuntimeException("Failed to connect to $url: Missing certificate root authority, try updating Java")
            throw e
        }
    }

    static String getLatestForgeVersion(String mcVersion) {
        final json = new JsonSlurper().parseText(new URL('https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json').getText('UTF-8'))
        final ver = json.promos["$mcVersion-latest"]
        ver === null ? null : (mcVersion + '-' + ver)
    }

    @CompileStatic
    static void processClassNodes(File file, @ClosureParams(value = SimpleType, options = 'org.objectweb.asm.tree.ClassNode') Closure process) {
        file.withInputStream { i ->
            new ZipInputStream(i).withCloseable { zin ->
                ZipEntry zein
                while ((zein = zin.nextEntry) !== null) {
                    if (zein.name.endsWith('.class')) {
                        var node = new ClassNode(ASM_LEVEL)
                        new ClassReader(zin).accept(node, 0)
                        process(node)
                    }
                }
            }
        }
    }
}
