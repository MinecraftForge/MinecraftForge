import os, os.path, sys

from fml import setup_fml, finish_setup_fml, apply_fml_patches, setup_mcp

def fml_main(fml_dir, mcp_dir, dont_gen_conf=True):
    print '================ Forge ModLoader Setup Start ==================='
    setup_mcp(fml_dir, mcp_dir, dont_gen_conf)
    setup_fml(fml_dir, mcp_dir)
    apply_fml_patches(fml_dir, mcp_dir, os.path.join(mcp_dir, 'src'))
    finish_setup_fml(fml_dir, mcp_dir)
    print '================  Forge ModLoader Setup End  ==================='
    
if __name__ == '__main__':
    fml_main(os.path.dirname(os.path.abspath(__file__)), os.path.abspath('..'))