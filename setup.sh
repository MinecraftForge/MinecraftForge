#!/bin/bash
$(which python2 || which python || echo python) setup.py "$@"
