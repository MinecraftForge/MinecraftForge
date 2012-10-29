import os, os.path, sys, re
import json, urllib2
from pprint import pprint
from urlparse import urlparse
import base64
import optparse, ast

class PreemptiveBasicAuthHandler(urllib2.BaseHandler):
	def __init__(self, password_mgr=None):
		if password_mgr is None:
			password_mgr = urllib2.HTTPPasswordMgrWithDefaultRealm()
		self.passwd = password_mgr
		self.add_password = self.passwd.add_password

	def http_request(self,req):
		uri = req.get_full_url()
		user, pw = self.passwd.find_user_password(None,uri)
		if pw is None: return req
		raw = "%s:%s" % (user, pw)
		auth = 'Basic %s' % base64.b64encode(raw).strip()
		req.add_unredirected_header('Authorization', auth)
		return req

	def https_request(self,req):
		return self.http_request(req)

def getBuildInfo(url):
	handler = PreemptiveBasicAuthHandler()
	handler.add_password(None, 'jenkins.minecraftforge.net', 'console_script', 'd29a4bb55ecdf4f99295c77cc4364fe6')
	file = urllib2.build_opener(handler).open(url)
	data = file.read()
	file.close()
	data = ast.literal_eval(data)['builds']
	data = sorted(data, key=lambda key: key['number'], reverse=True)
	for build in data:
		build['actions'] = filter(lambda act: 'text' in act, build['actions'])
		build['actions'] = filter(lambda act: not ' ' in act['text'], build['actions'])
		if len(build['actions']) == 0:
			build['version'] = None
		else:
			build['version'] = build['actions'][0]['text']
		build['items'] = build['changeSet']['items']
		for item in build['items']:
			item['author'] = item['author']['fullName']
		build.pop('changeSet')
		build.pop('actions')
	#pprint(data)
	return data

def main():
	target_build = 0
	job_path = None
	change_file = None
	
	if len(sys.argv) > 3:
		try:
			target_build = int(sys.argv[1])
			job_path = sys.argv[2]
			change_file = sys.argv[3]
		except:
			pass
	else:
		target_build = 10000
		job_path = "http://jenkins.minecraftforge.net/job/forge/"
		change_file = "changes.txt"
	p = re.compile('\w+-(?P<type>\w+)-(?P<version>[\d]+.[\d]+.[\d]+.(?P<build>[\d]+)).zip')
	builds = {}
	for filename in os.listdir('D:\\tmp'):
		m = p.search(filename)
		if not m is None:
			builds[m.group('build')] = m.group('version')
	#pprint(builds)
	
	for key	in sorted(builds.iterkeys(), key=lambda key: int(key)):
		print '%s %s' % (key, builds[key])
		make_changelog(job_path, int(key), 'd:\\tmp\minecraftforge-changelog-%s.txt' % builds[key])

def make_changelog(job_path, target_build, change_file):
	builds = getBuildInfo('%s/api/python?tree=builds[number,actions[text],changeSet[items[author[fullName],comment]]]&pretty=true' % job_path)
	#pprint(builds)
	
	log = [ "Changelog:" ]
	
	for build in builds:
		if int(build['number']) > target_build: continue
		if len(build['items']) == 0: continue
		log.append('')
		if build['version'] is None:
			log.append('Build %s' % build['number'])
		else:
			log.append('Build %s' % build['version'])
		for change in build['items']:
			comments = filter(lambda cmt: len(cmt) > 0, change['comment'].split('\n'))
			if len(comments) > 1:
				log.append('\t' + change['author'])
				for comment in comments:
					log.append('\t\t' + comment)
			elif len(comments) == 1:
				log.append('\t%s: %s' % (change['author'], comments[0]))
				
	#print '\n'.join(log)
	file = open(change_file, 'wb')
	for line in log:
		file.write('%s\n' % line)
	file.close()

if __name__ == '__main__':
	main()