import urllib
import zipfile

if __name__ == '__main__':
    try:
        urllib.urlretrieve("http://goo.gl/PnJHp", './fernflower.zip')
        zf = zipfile.ZipFile('fernflower.zip')
        zf.extract('fernflower.jar', '../runtime/bin')
    except:
        print "Downloading Fernflower failed download manually from http://goo.gl/PnJHp"