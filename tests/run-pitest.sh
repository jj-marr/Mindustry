#!/usr/bin/env bash

# Print verbose information
echo "======================================================================"
echo "Starting Pitest mutation testing on MapMarkers in ephemeral nix-shell..."
echo "======================================================================"

# Clean previous pitest results
echo "Cleaning previous reports..."
rm -rf ./build/reports/pitest

# Set PITEST_DEBUG to help with debugging
export PITEST_DEBUG="true"

# Run in nix-shell
echo "Running Pitest through Gradle..."
cd "$(dirname "$0")"  # Ensure we're in the tests directory
nix-shell --pure ../shell.nix --run "gradle pitest --info"

# Check if reports were generated
REPORT_DIR="./build/reports/pitest"
if [ -d "$REPORT_DIR" ]; then
    echo ""
    echo "======================================================================"
    echo "Mutation testing complete!"
    echo "HTML reports available at: $REPORT_DIR"
    echo "Open index.html in that directory to view results"
    echo "======================================================================"
else
    echo ""
    echo "======================================================================"
    echo "Error: No reports were generated. Check for failures in the output above."
    echo "======================================================================"
fi
