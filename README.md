# SysIdCollector

When using the [SysId tool](https://docs.wpilib.org/en/stable/docs/software/pathplanning/system-identification/introduction.html), our team was having 
difficulties deploying the project code to our robot; it was crashing.

This is a re-implementation of the [Drivetrain sysid-project](https://github.com/wpilibsuite/sysid/tree/main/sysid-projects) 
n Java instead of C++, because, when Java crashes, it prints a stacktrace of where the error occurred.
