#!/bin/bash
set -e

change_admin_password_func.sh $GLASSFISH_PASS

enable_secure_admin.sh $GLASSFISH_PASS
