#!/bin/bash
$(which python2 || which python || echo python) update_patches.py "$@"
