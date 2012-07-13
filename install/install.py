import os, os.path, sys

fml_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')

from fml import setup_fml, finish_setup_fml

def main():
    print '================ Forge ModLoader Setup Start ==================='
    setup_fml(fml_dir, mcp_dir)
    finish_setup_fml(fml_dir, mcp_dir)
    print '================  Forge ModLoader Setup End  ==================='
    
if __name__ == '__main__':
    main()