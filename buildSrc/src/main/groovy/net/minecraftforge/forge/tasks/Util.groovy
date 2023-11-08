package net.minecraftforge.forge.tasks

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

public class Util {
    static final ASM_LEVEL = Opcodes.ASM9

	static void init() {
		File.metaClass.sha1 = { ->
			MessageDigest md = MessageDigest.getInstance('SHA-1')
			delegate.eachByte 4096, {bytes, size ->
				md.update(bytes, 0, size)
			}
			return md.digest().collect {String.format "%02x", it}.join()
		}
        File.metaClass.getSha1 = { !delegate.exists() ? null : delegate.sha1() }
		File.metaClass.sha256 = { ->
			MessageDigest md = MessageDigest.getInstance('SHA-256')
			delegate.eachByte 4096, {bytes, size ->
				md.update(bytes, 0, size)
			}
			return md.digest().collect {String.format "%02x", it}.join()
		}
        File.metaClass.getSha256 = { !delegate.exists() ? null : delegate.sha256() }

		File.metaClass.json = { -> new JsonSlurper().parseText(delegate.text) }
        File.metaClass.getJson = { return delegate.exists() ? new JsonSlurper().parse(delegate) : [:] }
        File.metaClass.setJson = { json -> delegate.text = new JsonBuilder(json).toPrettyString() }

		Date.metaClass.iso8601 = { ->
			def format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
			def result = format.format(delegate)
			return result[0..21] + ':' + result[22..-1]
		}

        String.metaClass.rsplit = { String del, int limit = -1 ->
            def lst = new ArrayList()
            def x = 0, idx
            def tmp = delegate
            while ((idx = tmp.lastIndexOf(del)) != -1 && (limit == -1 || x++ < limit)) {
                lst.add(0, tmp.substring(idx + del.length(), tmp.length()))
                tmp = tmp.substring(0, idx)
            }
            lst.add(0, tmp)
            return lst
        }
	}

	public static String[] getClasspath(project, libs, artifact) {
		def ret = []
		artifactTree(project, artifact).each { key, lib ->
			libs[lib.name] = lib
			if (lib.name != artifact)
				ret.add(lib.name)
		}
		return ret
	}

	public static def getArtifacts(project, config) {
		def ret = [:]
		config.resolvedConfiguration.resolvedArtifacts.each { dep ->
            def info = getMavenInfoFromDep(dep)
			def url = "https://libraries.minecraft.net/$info.path"
			if (!checkExists(url))
				url = "https://maven.minecraftforge.net/$info.path"
                
			ret[info.key] = [
				name: info.name,
				downloads: [
					artifact: [
						path: info.path,
						url: url,
						sha1: dep.file.sha1(),
						size: dep.file.length()
					]
				]
			]
		}
		return ret
	}
    
    public static def getMavenInfoFromDep(dep) {
        return getMavenInfoFromMap([
            group: dep.moduleVersion.id.group,
            name: dep.moduleVersion.id.name,
            version: dep.moduleVersion.id.version,
            classifier: dep.classifier,
            extension: dep.extension
        ])
    }
    public static def getMavenInfoFromTask(task) {
        return getMavenInfoFromMap([
            group: task.project.group,
            name: task.project.name,
            version: task.project.version,
            classifier: task.archiveClassifier.get(),
            extension: task.archiveExtension.get()
        ])
    }
    
    private static def getMavenInfoFromMap(art) {
        def key = "$art.group:$art.name"
        def name = "$art.group:$art.name:$art.version"
        def path = "${art.group.replace('.', '/')}/$art.name/$art.version/$art.name-$art.version"
        if (art.classifier != null) {
            name += ":$art.classifier"
            path += "-$art.classifier"
        }
        if (!'jar'.equals(art.extension)) {
            name += "@$art.extension"
            path += ".$art.extension"
        } else {
            path += ".jar"
        }
        return [
            key: key,
            name: name,
            path: path,
            art: art
        ]
    }

	public static def iso8601Now() { new Date().iso8601() }

	public static def sha1(file) {
		MessageDigest md = MessageDigest.getInstance('SHA-1')
		file.eachByte 4096, {bytes, size ->
			md.update(bytes, 0, size)
		}
		return md.digest().collect {String.format "%02x", it}.join()
	}

	private static def artifactTree(project, artifact, transitive = true) {
		if (!project.ext.has('tree_resolver'))
			project.ext.tree_resolver = 1
		def cfg = project.configurations.create('tree_resolver_' + project.ext.tree_resolver++)
        cfg.transitive = transitive
		def dep = project.dependencies.create(artifact)
		cfg.dependencies.add(dep)
		def files = cfg.resolve()
		return getArtifacts(project, cfg)
	}

	static boolean checkExists(url) {
		try {
			def code = new URL(url).openConnection().with {
				requestMethod = 'HEAD'
				connect()
				responseCode
			}
			return code == 200
		} catch (Exception e) {
			if (e.toString().contains('unable to find valid certification path to requested target'))
				throw new RuntimeException('Failed to connect to ' + url + ': Missing certificate root authority, try updating java')
			throw e
		}
	}

    static String getLatestForgeVersion(mcVersion) {
        final json = new JsonSlurper().parseText(new URL('https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json').getText('UTF-8'))
        final ver = json.promos["$mcVersion-latest"]
        ver === null ? null : (mcVersion + '-' + ver)
    }

    static void processClassNodes(File file, Closure process) {
        file.withInputStream { i ->
            new ZipInputStream(i).withCloseable { zin ->
                ZipEntry zein
                while ((zein = zin.nextEntry) != null) {
                    if (zein.name.endsWith('.class')) {
                        def node = new ClassNode(ASM_LEVEL)
                        new ClassReader(zin).accept(node, 0)
                        process(node)
                    }
                }
            }
        }
    }
}