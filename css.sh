#!/bin/bash

echo "Building css..."
sass src/scss/styles.scss | autoprefixer --map --output resources/public/css/styles.css
