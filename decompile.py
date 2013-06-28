import urllib
import zipfile
import sys
import os
from optparse import OptionParser


if __name__ == '__main__':
    parser = OptionParser()
    parser.add_option('-g', '--gen-conf', action="store_true", dest='gen_conf', help='Generate merged MCP conf folder', default=False)
    parser.add_option('-m', '--mcp-dir', action='store',       dest='mcp_dir',  help='Path to download/extract MCP to', default=None )
    parser.add_option('-f', '--fml-dir', action='store',       dest='fml_dir',  help='Path to FML install folder',      default=None )
    options, _ = parser.parse_args()
    
    if options.fml_dir is None or options.mcp_dir is None:
        print 'Invalid arguments, must supply mcp folder and fml folder: decompile.py --mcp-dir <MCPFolder> --fml-dir <FMLFolder>'
    else:
        sys.path.append(os.path.join(options.fml_dir, 'install'))
        
        from fml import decompile_minecraft, setup_mcp, download_mcp
        
        download_mcp(fml_dir=options.fml_dir, mcp_dir=options.mcp_dir)
        setup_mcp(options.fml_dir, options.mcp_dir, gen_conf=options.gen_conf)
        decompile_minecraft(options.fml_dir, options.mcp_dir)
