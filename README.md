The irresponsible clojure guild present...

# tv100

Combinators for data processing

This library combines two activities: validation and transformation (V and T).
Since two letters is too short and I don't like long names, I needed something a
bit longer than 'vt' or 'tv'. Handily there's prior art in this area:

> The VT100 is a video terminal, introduced in August 1978 by Digital Equipment
> Corporation (DEC). It was one of the first terminals to support ANSI escape
> codes for cursor control and other tasks, and added a number of extended codes
> for special features like controlling the LED lamps on the keyboard.

![logo](https://irresponsible.github.io/media/tv100/logo.png)

Logo courtesy of [openclipart](https://openclipart.org/detail/21303/old-television)

It amused me holding up the VT100 as an example of something worthy of naming
a library after, so here it is.

## Why?

When it comes down to it, we only deal in data and code, and in lisp, code is data.

As part of the search for "how to make programming less tedious", I realised that I
spend a lot of time validating and transforming data, so I combined them into one
activity as an experiment and in doing so created a convenient base upon which to
build a bunch of other useful modules I'm planning. Hope you like it.

## Usage

[![Clojars Project](http://clojars.org/irresponsible/tv100/latest-version.svg)](http://clojars.org/irresponsible/tv100)

[![Travis CI](https://travis-ci.org/irresponsible/tv100.svg?branch=master)](https://travis-ci.org/irresponsible/tv100)

```clojure
(ns myapp
  (:use [irresponsible.tv100]))

(def my-data-structure {:foo [{1 2}]})

(def validator
  "A very quickly composed validator that roughly resembles the structure"
  (comp tvmap? (tv-keys tvkey?)
        (tv-vals (comp tvvec? (tv-map comp tvmap? (tv-zip tvint? tvint?))))))
```

## Clojurescript support

Just works! Obviously you have to :require rather than :use as in the example.

The following functions are currently unimplemented:

* tvint? (try tvfloat?, i haven't figured out what to do about ES6 ints yet)
* tvclass? (javascript doesn't have a special 'class' type)
* tv-isa? (because it almost certainly wouldn't do what you want it to)
* tv-instance? (because i haven't written it yet)

## Docs

Sorry, just inline at the minute, but they're good quality.

## Contributing

If you have any issues, please open an issue on github. If you have any patches,
(including doc patches!), please open a pull request or an issue.

## License

Copyright (c) 2016 James Laver

MIT LICENSE

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
