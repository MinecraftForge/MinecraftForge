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

def getBuildInfo(url, current_version=None):
	handler = PreemptiveBasicAuthHandler()
	handler.add_password(None, 'jenkins.minecraftforge.net', 'console_script', 'd29a4bb55ecdf4f99295c77cc4364fe6')
	file = urllib2.build_opener(handler).open(url)
	data = file.read()
	file.close()
	data = ast.literal_eval(data)['allBuilds']
	data = sorted(data, key=lambda key: key['number'], reverse=False)
	
	items = []
	output = []
	
	for build in data:
		build['actions'] = filter(lambda act: act is not None, build['actions'])
		build['actions'] = filter(lambda act: 'text' in act, build['actions'])
		build['actions'] = filter(lambda act: not ' ' in act['text'], build['actions'])
		if len(build['actions']) == 0:
			build['version'] = current_version
			current_version = None
		else:
			build['version'] = build['actions'][0]['text']
		build['items'] = build['changeSet']['items']
		for item in build['items']:
			item['author'] = item['author']['fullName']
		if build['result'] != 'SUCCESS':
			items += build['items']
		else:
			build['items'] = items + build['items']
			items = []
			output += [build]
		build.pop('changeSet')
		build.pop('actions')
	return sorted(output, key=lambda key: key['number'], reverse=True)
	
def make_changelog(job_path, target_build, change_file, current_version=None):
	builds = getBuildInfo('%s/api/python?tree=allBuilds[result,number,actions[text],changeSet[items[author[fullName],comment]]]&pretty=true' % job_path, current_version)
	
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
				
	file = open(change_file, 'wb')
	for line in log:
		file.write('%s\n' % line)
	file.close()
	
if __name__ == '__main__':
	make_changelog("http://jenkins.minecraftforge.net/job/minecraftforge/", 70000, 'changelog.txt', 'pinecone')