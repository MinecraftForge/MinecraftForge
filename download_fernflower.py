import urllib
import zipfile
import sys
import os

if __name__ == '__main__':
    try:
        urllib.urlretrieve("http://goo.gl/PnJHp", './fernflower.zip')
        if len(sys.argv)>1:
          path = os.path.abspath(os.path.join(sys.argv[1],'runtime/bin'))
        else:
          path = os.path.abspath('../runtime/bin')
        zf = zipfile.ZipFile('fernflower.zip')
        zf.extract('fernflower.jar', path)
        print "Fernflower downloaded into MCP at %s" % (path)
    except:
        print "Downloading Fernflower failed download manually from http://goo.gl/PnJHp"
