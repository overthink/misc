# yes really
for i in xrange(1, 101):
    mod3 = (i % 3 == 0)
    mod5 = (i % 5 == 0)
    if mod3 and mod5:
        print "FizzBuzz"
    elif mod3:
        print "Fizz"
    elif mod5:
        print "Buzz"
    else:
        print i
