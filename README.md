# tv100

Combinators for data processing

This library combines two activities: validation and transformation (V and T).
Since two letters is too short and I don't like long names. Handily there's
prior art in this area:

> The VT100 is a video terminal, introduced in August 1978 by Digital Equipment
> Corporation (DEC). It was one of the first terminals to support ANSI escape
> codes for cursor control and other tasks, and added a number of extended codes
> for special features like controlling the LED lamps on the keyboard.

It amused me holding up the VT100 as an example of something worthy of naming
a library after, so here it is.

## Why?

When it comes down to it, we only deal in data and code, and in lisp, code is data.

As part of the search for "how to make programming less tedious", I realised that I
spend a lot of time validating and transforming data, so I combined them into one
activity as an experiment.

a convenient base upon which to build a bunch of other useful modules I'm planning.

## Usage

```clojure
(ns myapp
  (:use [tv100]))

(def my-data-structure {:foo [{1 2}]})

(def validator
  "A very quickly composed validator that roughly resembles the structure"
  (comp tvmap? (tvkeys tvkey)
        (tvvals (comp tvvec? (tv-map comp tvmap? (tv-zip tvint? tvint?))))))
```

## Quality and support

* I'll try not to break the API.
* I believe in both tests and documentation and try to make them both good.
* The project is considered supported as I use it for several others.

If you have any issues, please open an issue on github. If you have any patches,
(including doc patches!), please open a pull request.

## Docs

Generate with 'lein doc', read at doc/index.html

## Tests

Run with 'lein midje'

## TODO

* Figure out how to collect multiple errors
* Report line/col numbers:
 * Take an optional metadata map structure with line/col numbers
 * Make tools.reader spit out separate metadata structure we can use with the above
* More predicates and combiners
* Realisation tests in the test suite (check tv-or doesn't, tv-map does etc.)
** Note that I *think* these work currently. They *appear* to at least.

## License

Copyright © 2015 James Laver

Distributed under the MIT License (see LICENSE in this repository)
