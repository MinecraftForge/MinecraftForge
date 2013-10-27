import os, os.path, sys, re
import json
from pprint import pprint
import base64
import optparse, ast

# urllib changed from python2 to python3
if sys.version_info[0] < 3:
    from urllib2 import BaseHandler, HTTPPasswordMgrWithDefaultRealm, build_opener
    from urlparse import urlparse
else:
    from urllib.request import BaseHandler, HTTPPasswordMgrWithDefaultRealm, build_opener
    from urllib.parse import urlparse

class PreemptiveBasicAuthHandler(BaseHandler):
	def __init__(self, password_mgr=None):
		if password_mgr is None:
			password_mgr = HTTPPasswordMgrWithDefaultRealm()
		self.passwd = password_mgr
		self.add_password = self.passwd.add_password

	def http_request(self,req):
		uri = req.get_full_url()
		user, pw = self.passwd.find_user_password(None,uri)
		if pw is None: return req
		raw = ("%s:%s" % (user, pw)).encode('utf-8')
		auth = 'Basic %s' % base64.b64encode(raw).strip().decode('utf-8')
		req.add_unredirected_header('Authorization', auth)
		return req

	def https_request(self,req):
		return self.http_request(req)

def read_url(url):
	handler = PreemptiveBasicAuthHandler()
	handler.add_password(None, 'ci.jenkins.minecraftforge.net', 'console_script', 'dc6d48ca20a474beeac280a9a16a926e')
	file = build_opener(handler).open(url)
	data = file.read()
	file.close()
	return data

def getBuildInfo(url, current_version=None):
	# an old commit seems to have \x01 bytes at the beginning of the message (ew), strip them so that
	# json doesn't complain
	data = read_url(url).decode('utf-8').replace("\x01", "")
	data = json.loads(data)['allBuilds']
	data = sorted(data, key=lambda key: key['number'], reverse=False)
	
	items = []
	output = []
	
	for build in data:
		# we could probably fuse those 3 lines in one by moving the checks in a small def
		build['actions'] = [act for act in build['actions'] if act is not None]
		build['actions'] = [act for act in build['actions'] if 'text' in act]
		build['actions'] = [act for act in build['actions'] if not ' ' in act['text']]
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
    
def add_latest_build(url, builds, current_version=None):
    data = read_url(url).decode('utf-8')
    data = json.loads(data)
    number = data['number']
    
    if builds[0]['number'] == data['number']:
        return builds
    
    for item in data['changeSet']['items']:
        item['author'] = item['author']['fullName']
    
    build = {
        'number' : data['number'],
        'result' : 'SUCCESS', #Currently build should always be success... Else things derp after words
        'version' : current_version,
        'items' : data['changeSet']['items']
    }
    return [build] + builds    
	
def make_changelog(job_path, target_build, change_file, current_version=None):
	builds = getBuildInfo('%s/api/json?tree=allBuilds[result,number,actions[text],changeSet[items[author[fullName],comment]]]' % job_path, current_version)
	builds = add_latest_build('%s/lastBuild/api/json?tree=number,changeSet[items[author[fullName],comment]]' % job_path, builds, current_version)

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
			comments = [cmt for cmt in change['comment'].split('\n') if len(cmt) > 0]
			if len(comments) > 1:
				log.append('\t' + change['author'])
				for comment in comments:
					log.append('\t\t' + comment)
			elif len(comments) == 1:
				log.append('\t%s: %s' % (change['author'], comments[0]))
				
	with open(change_file, 'wb') as file:
		for line in log:
			file.write(('%s\n' % line).encode('utf-8'))
	
if __name__ == '__main__':
	make_changelog("http://ci.jenkins.minecraftforge.net/job/minecraftforge/", 70000, 'changelog.txt', 'pinecone')