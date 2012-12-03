import os, os.path, sys
from optparse import OptionParser

from fml import setup_fml, finish_setup_fml, apply_fml_patches, setup_mcp

def fml_main(fml_dir, mcp_dir, dont_gen_conf=True):
    print '================ Forge ModLoader Setup Start ==================='
    setup_mcp(fml_dir, mcp_dir, dont_gen_conf)
    setup_fml(fml_dir, mcp_dir)
    apply_fml_patches(fml_dir, mcp_dir, os.path.join(mcp_dir, 'src'))
    finish_setup_fml(fml_dir, mcp_dir)
    print '================  Forge ModLoader Setup End  ==================='
    
if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir', action='store_true', dest='mcp_dir', help='Path to download/extract MCP to', default=None)
    options, _ = parser.parse_args()
    
    fml_dir = os.path.dirname(os.path.abspath(__file__))

    if not options.mcp_dir is None:
        fml_main(fml_dir, os.path.abspath(options.mcp_dir))
    elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
        fml_main(fml_dir, os.path.abspath('..'))
    else:
        fml_main(fml_dir, os.path.abspath('mcp'))