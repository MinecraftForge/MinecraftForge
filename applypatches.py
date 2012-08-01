import os, sys
                    
def main():
    fml_dir = os.path.abspath(sys.argv[1])
    work_dir = os.path.normpath(sys.argv[2])
    mcp_dir = os.path.normpath(sys.argv[3])
    
    sys.path.append(os.path.join(fml_dir, 'install'))
    from fml import apply_fml_patches
    
    apply_fml_patches(fml_dir, mcp_dir, work_dir, False)
        
if __name__ == '__main__':
    main()
