#!/usr/bin/env bash

# run-pitest.sh
# Script to run Pitest mutation testing in an ephemeral nix-shell

# Print verbose information
echo "======================================================================"
echo "Starting Pitest mutation testing in ephemeral nix-shell..."
echo "Target classes: mindustry.game.*"
echo "Target tests: *Tests"
echo "This will analyze the MapMarkers class and related code in the core module"
echo "======================================================================"

# Set environment variables for Pitest to help with debugging
export PITEST_DEBUG="true"

# Clean previous pitest results
echo "Cleaning previous reports..."
rm -rf ./build/reports/pitest

# Run in nix-shell with more debugging output
echo "Running Pitest through Gradle..."
nix-shell --pure ./shell.nix --run "gradle pitest --info"

# Check if reports were generated
REPORT_DIR="./build/reports/pitest"
if [ -d "$REPORT_DIR" ]; then
    echo ""
    echo "======================================================================"
    echo "Mutation testing complete!"
    echo "HTML reports available at: $REPORT_DIR"
    echo "Open index.html in that directory to view results"
    echo "======================================================================"

    # Try to count mutations
    MUTATIONS=$(grep -r "generated" "$REPORT_DIR" | grep -o "[0-9]* mutations" | head -1)
    if [ ! -z "$MUTATIONS" ]; then
        echo "Summary: $MUTATIONS were generated and tested"
    fi
else
    echo ""
    echo "======================================================================"
    echo "Error: No reports were generated. Check for failures in the output above."
    echo "======================================================================"
fi
