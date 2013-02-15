#!/bin/bash
PYTHON=python2
if ! which "$PYTHON" >/dev/null; then
	PYTHON=python
fi
$PYTHON install.py
