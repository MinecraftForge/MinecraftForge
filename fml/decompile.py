import urllib
import zipfile
import sys
import os


if __name__ == '__main__':
    if not len(sys.argv) == 3:
        print 'Invalid arguments, must supply mcp folder and fml folder: decompile.y <MCPFolder> <FMLFolder>'
    else:
        mcp_dir = os.path.abspath(sys.argv[1])
        fml_dir = os.path.abspath(sys.argv[2])
        
        sys.path.append(os.path.join(fml_dir, 'install'))
        
        from fml import setup_fml
        
        setup_fml(fml_dir, mcp_dir)
