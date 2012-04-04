#! /usr/bin/env python

import sys, re, os
data = open(sys.argv[1], "rb").read()
newdata = re.sub("\r?\n", "\r\n", data)
f = open(sys.argv[2], "wb")
f.write(newdata)
f.close()