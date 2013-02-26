import os, os.path, sys
from optparse import OptionParser

from fml import setup_fml, finish_setup_fml, apply_fml_patches, setup_mcp

def fml_main(fml_dir, mcp_dir, dont_gen_conf=True, disable_patches=False, disable_at=False, disable_merge=False, enable_server=False, disable_client=False,
    disable_rename=False):
    print '================ Forge ModLoader Setup Start ==================='
    setup_mcp(fml_dir, mcp_dir, dont_gen_conf)
    setup_fml(fml_dir, mcp_dir, disable_at=disable_at, disable_merge=disable_merge, enable_server=enable_server, disable_client=disable_client)
    if disable_patches:
        print 'Patching disabled'
    else:
        apply_fml_patches(fml_dir, mcp_dir, os.path.join(mcp_dir, 'src'))
    finish_setup_fml(fml_dir, mcp_dir, enable_server=enable_server, disable_client=disable_client, disable_rename=disable_rename)
    print '================  Forge ModLoader Setup End  ==================='
    
if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-m', '--mcp-dir',   action='store_true', dest='mcp_dir',       help='Path to download/extract MCP to',         default=None )
    parser.add_option('-p', '--no-patch',  action="store_true", dest='no_patch',      help='Disable application of FML patches',      default=False)
    parser.add_option('-a', '--no-access', action="store_true", dest='no_access',     help='Disable access transformers',             default=False)
    parser.add_option('-s', '--server',    action="store_true", dest='enable_server', help='Enable decompilation of server',          default=False)
    parser.add_option('-c', '--no-client', action="store_true", dest='no_client',     help='Disable decompilation of server',         default=False)
    parser.add_option('-e', '--no-merge',  action="store_true", dest='no_merge',      help='Disable merging server code into client', default=False)
    parser.add_option('-n', '--no-rename', action="store_true", dest='no_rename',     help='Disable running updatenames',             default=False)
    options, _ = parser.parse_args()
    
    fml_dir = os.path.dirname(os.path.abspath(__file__))
    mcp_dir = os.path.abspath('mcp')

    if not options.mcp_dir is None:
        mcp_dir = os.path.abspath(options.mcp_dir)
    elif os.path.isfile(os.path.join('..', 'runtime', 'commands.py')):
        mcp_dir = os.path.abspath('..')
    
    if options.no_client:
        options.no_patch = True
        
    if options.no_merge:
        options.no_patch = True
    
    fml_main(fml_dir, mcp_dir, disable_patches=options.no_patch, 
        disable_at=options.no_access, disable_merge=options.no_merge,
        enable_server=options.enable_server, disable_client=options.no_client,
        disable_rename=options.no_rename)