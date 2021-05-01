package net.minecraftforge.forge.tasks

import groovy.json.JsonSlurper

import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.AbbreviatedObjectId
import org.eclipse.jgit.lib.ObjectId

public class Util {
	static void init() {
		File.metaClass.sha1 = { ->
			MessageDigest md = MessageDigest.getInstance('SHA-1')
			delegate.eachByte 4096, {bytes, size ->
				md.update(bytes, 0, size)
			}
			return md.digest().collect {String.format "%02x", it}.join()
		}
	
		File.metaClass.json = { -> new JsonSlurper().parseText(delegate.text) }
		
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
	
	public static def getArtifacts(project, config, classifiers) {
		def ret = [:]
		config.resolvedConfiguration.resolvedArtifacts.each {
			def art = [
				group: it.moduleVersion.id.group,
				name: it.moduleVersion.id.name,
				version: it.moduleVersion.id.version,
				classifier: it.classifier,
				extension: it.extension,
				file: it.file
			]
			def key = art.group + ':' + art.name
			def folder = "${art.group.replace('.', '/')}/${art.name}/${art.version}/"
			def filename = "${art.name}-${art.version}"
			if (art.classifier != null)
				filename += "-${art.classifier}"
			filename += ".${art.extension}"
			def path = "${folder}${filename}"
			def url = "https://libraries.minecraft.net/${path}"
			if (!checkExists(url)) {
				url = "https://maven.minecraftforge.net/${path}"
			}
			//TODO remove when Mojang launcher is updated
			if (!classifiers && art.classifier != null) { 
				//Mojang launcher doesn't currently support classifiers, so... move it to part of the version, and force the extension to 'jar'
				// However, keep the path normal so that our mirror system works.
				art.version = "${art.version}-${art.classifier}"
				art.classifier = null
				art.extension = 'jar'
			}
			ret[key] = [
				name: "${art.group}:${art.name}:${art.version}" + (art.classifier == null ? '' : ":${art.classifier}") + (art.extension == 'jar' ? '' : "@${art.extension}"),
				downloads: [
					artifact: [
						path: path,
						url: url,
						sha1: sha1(art.file),
						size: art.file.length()
					]
				]
			]
		}
		return ret
	}

	public static def iso8601Now() { new Date().iso8601() }

	public static def sha1(file) {
		MessageDigest md = MessageDigest.getInstance('SHA-1')
		file.eachByte 4096, {bytes, size ->
			md.update(bytes, 0, size)
		}
		return md.digest().collect {String.format "%02x", it}.join()
	}

	private static def artifactTree(project, artifact) {
		if (!project.ext.has('tree_resolver'))
			project.ext.tree_resolver = 1
		def cfg = project.configurations.create('tree_resolver_' + project.ext.tree_resolver++)
		def dep = project.dependencies.create(artifact)
		cfg.dependencies.add(dep)
		def files = cfg.resolve()
		return getArtifacts(project, cfg, true)
	}
	
	private static boolean checkExists(url) {
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
    
    public static def gitInfo(dir) {
        init()
        def git = null
        try {
            git = Git.open(dir)
        } catch (org.eclipse.jgit.errors.RepositoryNotFoundException e) {
            return [
                tag: '0.0',
                offset: '0',
                hash: '00000000',
                branch: 'master',
                commit: '0000000000000000000000',
                abbreviatedId: '00000000'
            ]
        }
        def desc = git.describe().setLong(true).setTags(true).call().rsplit('-', 2)
        def head = git.repository.resolve('HEAD')
        
        def ret = [:]
        ret.tag = desc[0]
        ret.offset = desc[1]
        ret.hash = desc[2]
        ret.branch = git.repository.branch
        ret.commit = ObjectId.toString(head)
        ret.abbreviatedId = head.abbreviate(8).name()
        
        return ret
    }
    
    public static String getVersion(info, mc_version) {
        def branch = info.branch
        if (branch != null && branch.startsWith('pulls/'))
            branch = 'pr' + branch.rsplit('/', 1)[1]
        if (branch in [null, 'master', 'HEAD', mc_version, mc_version + '.0', mc_version.rsplit('.', 1)[0] + '.x'])
            return "${mc_version}-${info.tag}.${info.offset}".toString()
        return "${mc_version}-${info.tag}.${info.offset}-${branch}".toString()
    }
}