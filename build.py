import os, os.path, sys
from optparse import OptionParser

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')

from forge import build_forge_dev

def main():
    
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store', dest='mcp_dir', help='MCP Path', default=None)
    parser.add_option('-b', '--build', action='store', dest='build', help='Build number', default=None)
    options, _ = parser.parse_args()
    
    build_num = 0
    if not options.build is None:
        try:
            build_num = int(options.build)
        except:
            pass
    
    fml_dir = os.path.join(forge_dir, 'fml')
    mcp_dir = os.path.join(forge_dir, 'mcp')

    if not options.mcp_dir is None:
        mcp_dir = os.path.abspath(options.mcp_dir)
    elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
        mcp_dir = os.path.abspath('..')
        
    sys.exit(build_forge_dev(mcp_dir, forge_dir, fml_dir, build_num))
    
if __name__ == '__main__':
    main()