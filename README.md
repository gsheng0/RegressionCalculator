## Table of contents
* [General info](#general-info)
* [Algorithm](#algorithm)
* [Screenshots](#screenshots)

## General info
Regression calculator is a linear regression calculator written entirely in Java 8. 
Users enter points as ordered pairs, or click on the window to simulate "data"
A linear line of best fit will be generated to fit the data

## Algorithm
This calculator uses a rudimentary gradient descent algorithm to slowly minimize the cost of a test function over a large number of iterations

When points are plotted, the magnitued of the vertical distance between that point and the current iteration of the function at the same x point is squared and then summed up for each point
This forms the "cost function" or how far off the function is from being perfect.

This can be expressed mathematically as the following:

![alt text](https://github.com/gsheng0/RegressionCalculator/blob/master/Screen%20Shot%202021-03-24%20at%2012.22.37%20AM.png?raw=true)

Where *f(x)* is the current iteration of the linear best fit function with coefficient *a* and constant term *b*

Then, by using calculus, the function is able to minimized using gradient descent by deriving this cost function with respect to *a* and *b*, resulting in the cumulative gradient of all the points for each of the two parameters of the eventual line of best fit function.

The gradient function for *a* looks like this:


