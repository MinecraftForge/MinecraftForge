import subprocess, sys, os
from pprint import pformat
from optparse import OptionParser

def run_command(command, cwd='.'):
    #print('Running command: ')
    process = subprocess.Popen(command, stdout=subprocess.PIPE, stdin=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=1, cwd=cwd)
    out, err = process.communicate()
    out = out.strip('\n')
    #print(pformat(out.split('\n')))
    if process.returncode:
        print('failed: %d' % process.returncode)
        return None
    return out.split('\n')
  
def main(options, args):
    output = run_command(['git', 'diff', '--no-color', '--', 'fml'])
    if output is None:
        print('Failed to grab submodule commits')
        sys.exit(1)
    
    start = None
    end = None
    for line in output:
        if not 'Subproject commit' in line:
            continue
        if line[0:18] == '-Subproject commit':
            start = line[19:]
        elif line[0:18] == '+Subproject commit':
            end = line[19:]
    
    if start == None or end == None:
        print('Could not extract start and end range')
        sys.exit(1)
    
    output = run_command(['git', 'log', '--reverse', '--pretty=oneline', '%s...%s' % (start, end)], './fml')
    print('Updated FML:')
    for line in output:
        print('MinecraftForge/FML@%s' % line)
    
    
if __name__ == '__main__':
    parser = OptionParser()
    options, args = parser.parse_args()
    
    main(options, args)