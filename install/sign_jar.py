import os, os.path, sys, csv, re, fnmatch, shutil, zipfile, pprint
from optparse import OptionParser
from zipfile import ZipFile
from subprocess import Popen, PIPE, STDOUT
    
if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-j', '--jar', action='store', dest='jar', help='Jar to sign', default=None)
    parser.add_option('-c', '--command', action='store', dest='command', help='Path to jar signer command, will be invoked on temp jar to sign', default=None)
    parser.add_option('-a', '--alias', action='store', dest='alias', help='Optional: The keystore alias to use when siging the jar, it will be passed in as para 2 to the command', default='')
    #This is kept as a seperate file so that if you integrate this into a build system, you are not publically displaying any private passwords
    #Typically the format of this file will be:
    #
    #jarsigner -keystore my_key_store -storepass key_store_pass -keypass key_pass %1 alias
    
    #The rest of the parameters are used as regxs to determine what parts of the file will be signed, a file will be signed if it matches ANY of the regexs
    options, args = parser.parse_args()
    
    if options.jar is None:
        print 'You must supply the jar to sign using --jar <jar>'
        sys.exit(1)
    
    if options.command is None:
        print 'You must supply a command to be called using --command <command>, See the comments in the python for details on this'
        sys.exit(1)
        
    if not os.path.isfile(options.jar):
        print 'The specified jar "%s" could not be found' % options.jar
        sys.exit(1)
        
    regs = []
    for reg in args:
        regs.append(re.compile(reg))
    
    shutil.copy(options.jar, options.jar + '.bak')
    tmp_1 = options.jar + '.tmp1'
    tmp_2 = options.jar + '.tmp2'
    
    zip_in = ZipFile(options.jar, mode='r')
    zip_tmp_1 = ZipFile(tmp_1, mode='w')
    zip_tmp_2 = ZipFile(tmp_2, mode='w')
    for i in zip_in.filelist:
        matched = False
        for reg in regs:
            if not reg.match(i.filename) is None:
                matched = True
                break
        
        data = zip_in.read(i.filename)
        if matched:
            print 'Matched: ' + i.filename
            zip_tmp_1.writestr(i, data)
        else:
            if i.filename.startswith('META-INF'):
                print 'Detected skipped META-INF File, it will not be copied to new jar, if you wish to keep this data, include it in the signed info: %s'  % i.filename
            else:
                print 'Skipped: ' + i.filename
                zip_tmp_2.writestr(i, data)
            
    zip_in.close()
    zip_tmp_1.close()
    zip_tmp_2.close()
    
    try:
        process = Popen([options.command, os.path.abspath(tmp_1), options.alias], stdout=PIPE, stderr=STDOUT, bufsize=-1)
        out, _ = process.communicate()
        print out
    except OSError as e:
        print "Error creating signed tmp jar: %s" % e.strerror
        sys.exit(1)
        
    os.remove(options.jar)
    
    zip_out = ZipFile(options.jar, mode='w')
    
    for tmp in [tmp_1, tmp_2]:
        zip_in = ZipFile(tmp, mode='r')
        
        for i in zip_in.filelist:
            zip_out.writestr(i, zip_in.read(i.filename))
        
        zip_in.close()
        
    zip_out.close()
    
    os.remove(tmp_1)
    os.remove(tmp_2)
    
    print 'Signing jar complete'
    