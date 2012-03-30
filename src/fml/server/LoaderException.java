package fml.server;

class LoaderException extends RuntimeException {
  /**
   * 
   */
  private static final long serialVersionUID = -5675297950958861378L;

  public LoaderException(Exception wrapped) {
    super(wrapped);
  }

  public LoaderException() {
  }
}