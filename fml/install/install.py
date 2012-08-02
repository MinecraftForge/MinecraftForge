import os, os.path, sys

fml_dir = os.path.dirname(os.path.abspath(__file__))
mcp_dir = os.path.abspath('..')

from fml import setup_fml, finish_setup_fml, apply_fml_patches, setup_mcp

def main():
    print '================ Forge ModLoader Setup Start ==================='
    setup_mcp(fml_dir, mcp_dir)
    setup_fml(fml_dir, mcp_dir)
    apply_fml_patches(fml_dir, mcp_sir, os.path.join(mcp_dir, 'src'))
    finish_setup_fml(fml_dir, mcp_dir)
    print '================  Forge ModLoader Setup End  ==================='
    
if __name__ == '__main__':
    main()