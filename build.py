import os, os.path, sys

forge_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')

from forge import build_forge_dev

def main():
    build_num = 0
    
    if len(sys.argv) > 1:
        try:
            build_num = int(sys.argv[1])
        except:
            pass
    fml_dir = os.path.join(forge_dir, 'fml')
    sys.exit(build_forge_dev(mcp_dir, forge_dir, fml_dir, build_num))
    
if __name__ == '__main__':
    main()