# Pitest Mutation Testing for Mindustry

Pitest (or PIT) is a state-of-the-art mutation testing system that's now integrated with the Mindustry project. Mutation testing helps identify weaknesses in your test suite by making small changes (mutations) to your code and checking if your tests catch these changes.

## How It's Set Up

The system is configured in:
1. The main `build.gradle` file (for global settings)
2. The `tests/build.gradle` file (for module-specific settings)
3. Both `run-pitest.sh` scripts - one in the root and one in the tests directory

## Running Mutation Tests

You have two options to run Pitest:

1. From root directory:
```bash
./run-pitest.sh
```

2. From tests directory:
```bash
cd tests
./run-pitest.sh
```

Both commands will execute Pitest in an ephemeral nix-shell environment and generate reports.

## Configuration

The current configuration:

```groovy
pitest {
    targetClasses = ['mindustry.game.MapMarkers']
    pitestVersion = '1.15.0'
    threads = 4
    outputFormats = ['HTML', 'XML']
    timestampedReports = false
    mutationThreshold = 0
    coverageThreshold = 0
    junit5PluginVersion = '1.0.0'
    verbose = true
    targetTests = ['MapMarkerTests']
    reportDir = file("${project.buildDir}/reports/pitest")
    failWhenNoMutations = false
    skipFailingTests = true

    // Enhanced set of mutation operators
    mutators = [
        // Standard mutators
        'CONDITIONALS_BOUNDARY',      // e.g. > changed to >=
        'INCREMENTS',                 // e.g. i++ changed to i--
        'INVERT_NEGS',                // e.g. -i changed to i
        'MATH',                       // e.g. + changed to -
        'NEGATE_CONDITIONALS',        // e.g. == changed to !=
        'VOID_METHOD_CALLS',          // Removes method calls to void methods
        'EMPTY_RETURNS',              // e.g. return 0; -> return 0;
        'FALSE_RETURNS',              // e.g. return true; -> return false;
        'TRUE_RETURNS',               // e.g. return false; -> return true;
        'NULL_RETURNS',               // e.g. return x; -> return null;

        // Collection-specific mutators (relevant for IntMap and Seq usage)
        'REMOVE_CONDITIONALS',         // Removes conditional statements
        'EXPERIMENTAL_MEMBER_VARIABLE',// Mutates member variables
        'EXPERIMENTAL_SWITCH',         // Mutates switch statements
        'CONSTRUCTOR_CALLS',           // Mutates constructor calls
        'NON_VOID_METHOD_CALLS',       // Mutates return values from non-void methods
        'ARGUMENT_PROPAGATION',        // Changes method parameters
        'INLINE_CONSTS',               // Mutates inline constants
        'RETURN_VALS'                  // Mutates return values
    ]

    mainSourceSets = [project(':core').sourceSets.main]
    testSourceSets = [sourceSets.test]
}
```

To test other classes, modify the `targetClasses` and `targetTests` parameters.

## Understanding Results

The HTML reports in `tests/build/reports/pitest` provide:

1. **Line Coverage**: Percentage of code lines executed by tests
2. **Mutation Coverage**: Percentage of mutants "killed" by tests
3. **Detailed breakdown**: Per-class and per-mutation-type analysis

## Current Status

The initial run shows:
- Line Coverage: 0/30 (0%)
- 15 mutations generated, 0 killed (0%)
- All mutations have NO_COVERAGE status

This indicates that while there are tests for MapMarkers, they are either:
1. Not executing the code in the MapMarkers class
2. Running but failing (hence we needed to set `skipFailingTests=true`)

## Improving Coverage

To improve the mutation coverage:

1. Ensure tests are correctly targeting the MapMarkers class
2. Add assertions that verify the expected behavior
3. Fix any failing tests
4. Gradually increase test coverage

Once tests are passing normally, you can remove the `skipFailingTests=true` setting to ensure a proper mutation analysis.

## Extending to Other Classes

Once the testing approach is working well for MapMarkers, you can expand to test other classes by:

1. Modifying `targetClasses` to include more classes (e.g., `'mindustry.game.*'` for all game classes)
2. Adjusting `targetTests` as needed
3. Setting appropriate thresholds to ensure good test quality (e.g., `mutationThreshold = 70`)

## Why Mutation Testing?

Traditional code coverage only tells you if code was executed, not if it was *tested effectively*. Mutation testing verifies that your tests can detect when code behavior changes, ensuring more robust test suites.
