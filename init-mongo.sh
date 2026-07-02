#!/bin/bash
set -e

mongosh <<EOF
db = db.getSiblingDB('ms_transaction_prod')
db.createCollection('transactions')
EOF
