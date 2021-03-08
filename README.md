`#%` _(`perc`)_
=============================

`perc` is a low-`char`b, keto-friendly function syntax sweetener for helping humans not say what does not need to be said.

# What is it?

Syntactically, `perc`s are very similar to Clojure's anonymous function syntax `#(Point. %1 %2 %3)`

However, in addition to these indexical references, `perc`s allow associative references by keywords, like so:

```clojure
#%(Point. %:x %:y %:z)
```

Sure, you've gotta add this one character after the hash - `%` - but the extra sweetness you get out of the other end is totally worth the squeeze: `%:the-name-you-already-gave-it`

# Getting Started

Place the following in the `:deps` map of your `deps.edn` file:

```clojure
  ...
  johnmn3/perc {:git/url "https://github.com/johnmn3/perc"
                :sha "4b8689986af25c7adb5935006cae15d190b307ce"}
  ...
```

If you want to test things out _right now_, from the comfort of your own `~/home`, go ahead and drop this in your bash pipe and smoke it:

```clojure
clj -Sdeps '{:deps {johnmn3/perc {:git/url "https://github.com/johnmn3/perc" :sha "4b8689986af25c7adb5935006cae15d190b307ce"}}}' -m cljs.main -c perc.core -re node -r
```

Then you should be able to test things out right away:

```clojure
Cloning: https://github.com/johnmn3/perc
Checking out: https://github.com/johnmn3/perc at 1c7e1d63aae9b2e59087ffc7774f6520b34e4c26
ClojureScript 1.10.520
cljs.user=> (#%(println "hi" %:x) {:x 1})
hi 1
nil
```

In ClojureScript, once a project is launched, you don't need to require anything because tagged literals work globally. In Clojure, you'll have to require `perc.core` in one of your project's namespaces.

# Overview

So, starting off from the basics, you know how Clojure has anonymous functions:

```clojure
((fn [{:keys [x y z]}] [(inc x) (inc y) (inc z)])
 {:x 1 :y 2 :z 3}) ; => [2 3 4]
```

And you also know how Clojure gives you a sugared syntax for that:

```clojure
(#(do [(inc (:x %)) (inc (:y %)) (inc (:z %))])
 {:x 1 :y 2 :z 3}) ; => [2 3 4]
```

Well, with `perc`s, you can instead refer to `(:x %)` as just `%:x`. We refer to these compound references with `%` as [path expressions](#basic-path-expressions), which act like a `get-in` into the passed in parameters, but they mostly still act how you'd expect them to with regard to the behavior of Clojure's existing sugared anonymous function syntax, like with `%`, `%1`, `%2`, `%&`, etc. So you can mostly use them as a drop in replacement.

So with [path expressions](#basic-path-expressions) you can do [named parameters](#named-anonymous-parameters) (`%:x`, `%1:x`), [namespaced named params](#namespaced-anonymous-parameters) (`%:*/x`, `%:*/s/valid?`), [keyword args](#keyword-arguments) (`%&:logging?`), or even a nested path like `%:x:y:z` (which becomes something like `(get-in % [:x :y :z])`) among others which we'll go over in the [details](#details) section below.

To use these [path expressions](#basic-path-expressions), all you have to do is, right before your function form, add a percent symbol (`%`) to the right of what would be the anoymous function's hash symbol. Like:

```clojure
(#%(do [(inc %:x) (inc %:y) (inc %:z)])
 {:x 1 :y 2 :z 3}) ; => [2 3 4]
```

Notice that we're returning a vector here - as such, we can instead lean on [return literals](#return-literals) to elide the evaluation of the list expression altogether, like so:

```clojure
(#%[(inc %:x) (inc %:y) (inc %:z)]
 {:x 1 :y 2 :z 3}) ; => [2 3 4]
```

And if we're dealing with nested values, we can drill into the data at any depth using the concatenatability of [path expressions](#advanced-path-expressions):

```clojure
(#%[(inc %:x:val) (inc %:y:val) (inc %:z:val)]
 {:x {:val 1} :y {:val 2} :z {:val 3}}) ; => [2 3 4]
```

When we're [mapping or reducing](#mapping-reducing) over a collection of maps, it can be quite eloquent:

```clojure
(->> [{:a 5 :b {:c 6}}
      {:a 7 :b {:c 8}}
      {:a 9 :b {:c 10}}]

      (mapv #%{:a (inc %:a)
               :b {:c (dec %:b:c)}})

      (reduce #%{:a (+ %1:a %2:a)
                 :b {:c (+ %1:b:c %2:b:c)}})) ; => {:a 24, :b {:c 21}}
```

Take a moment to reflect on the above expression's effectiveness in conveying only that which needs to be said and nothing more.

# Details

- [What is it?](#what-is-it?)
- [Getting Started](#getting-started)
- [Overview](#overview)
- [Details](#details)
  - [Basic Path Expressions](#basic-path-expressions)
  - [Named Anonymous Parameters](#named-anonymous-parameters)
  - [Namespaced Anonymous Parameters](#namespaced-anonymous-parameters)
  - [Return Literals](#return-literals)
  - [Advanced Path Expressions](#advanced-path-expressions)
  - [Mapping and Reducing](#mapping-and-reducing)
  - [Thread Fns](#thread-fns)
  - [Keyword Arguments](#keyword-arguments)
- [Nesting](#nesting)
  - [`%%` & `%%%`](#`%%`-&-`%%%`)
- [How](#how)
- [Why Not](#why-not)
- [Roadmap](#roadmap)

## Basic Path Expressions

The sections below go over each of `perc`'s features, showing a table of what expressions will result in what output code and providing some cannonical example code for each variation. The actual output code will look slightly different, but the `get-in` examples give you an idea of what is happening.

## Named anonymous parameters

Again, all you have to do is prepend your function body with `#%` - like you would normally use just `#` for with anonymous functions. Then you can use `%` and `%1` like usual, but you can also do `%:foo` or `%1:bar`.

```clojure
(defn Point [x y z]
  (str [x y z])) ; => #'cljs.user/Point

(#%(Point %:x %:y %:z)
 {:x 1 
  :y 2 
  :z 3}) ; => "[1 2 3]"
```

Note that if no index is given (like `%`) then the first param `%1` is implied.

We now might ask, why did we decide to keep Clojure's 1-based indexing for anonymymous parameters?

Answer: `perc`s are a read-time code notation designed for humans, not programmatic runtime generation. Because they're tag literals, they disappear after read time. They're literally not meant to be interpreted at runtime like other runtime data (just like Clojure's existing anonymous fn syntax), so it's not as if you'll be adding indexes to `perc`s expressions programmatically anyway. If you disagree with this decision and preferred we moved to 0-based indexes feel free to file an issue here and we can debate the merits.

|Path Expression|=>|Expression|
|---:|---:|:---|
|`%`| => | `(get-in % [0])`|
|`%:x`| => | `(get-in % [0 :x])`|
|`%:foo`| => | `(get-in % [0 :foo])`|

```clojure
(#%(println :%     % 
            :%:x   %:x 
            :%:foo %:foo)
 {:x   1 
  :foo 2})
; :% {:x 1, :foo 2} :%:x 1 :%:foo 2
```

## Namespaced Anonymous Parameters

Namespaced keywords do not work directly in `perc` for the JVM Clojure - consecutive colons (`::`) are not allowed within tokens. As a workaround, we place a `*` after the first colon of a keyword to indicate that the token should be converted to a namespaced keyword.

```clojure
(#%{:x %:*/x
    :y %:*s/y
    :z %:foreign/z}
   {::x        1 
    ::s/y      2 
    :foreign/z 3})
; {:x 1, :y 2, :z 3}
```

|Path Expression|=>|Expression|
|---:|---:|:---|
|`%:*/x`| => | `(get-in % [0 ::x])`|
|`%:*/foo`| => | `(get-in % [0 ::foo])`|
|`%:*foo/bar`| => | `(get-in % [0 ::foo/bar])`|

```clojure
(#%(println :%:*/x       %:*/x
            :%:*/foo     %:*/foo
            :%:*foo/bar  %:*foo/bar)
 {::x       1
  ::foo     2
  ::foo/bar 3})
; :%:*/x 1 :%:*/foo 2 :%:*/foo/bar 3
```

## Multiple parameters

Pulling named values out of multiple different parameters works as you'd expect.

```clojure
#%(response %1:ctx %2:status %2:body)
```

Here we grab `:ctx` from the first parameter, `:status` from the second and `:body` from the second.

This also makes it easier to deal with ambiguous keys coming in from multiple map sources. For example, suppose we want a function that takes three point maps and provides them to a `Triangle` constructor:

```clojure
#%(Triangle. %1:x %1:y %1:z,
             %2:x %2:y %2:z,
             %3:x %3:y %3:z)
```

Doing that with the regular old syntax, we would clobber coordinates if we tried to destructure using `:keys [x y z]`. And the alternative would be rather verbose and unnecessarily confusing:

```clojure
#(let [{x1 :x y1 :y z1 :z} %1
       {x2 :x y2 :y z2 :z} %2
       {x3 :x y3 :y z3 :z} %3]
   (Triangle. x1 y1 z1,
              x2 y2 z2,
              x3 y3 z3))
```

|Path Expression|=>|Expression|
|---:|---:|:---|
|`%1`| => | `(get-in % [0])`|
|`%2`| => | `(get-in % [1])`|
|`%1:x`| => | `(get-in % [0 :x])`|
|`%:x`| => | `(get-in % [0 :x])`|
|`%2:*/x`| => | `(get-in % [1 ::x])`|
|`%2:foo`| => | `(get-in % [1 :foo])`|
|`%3:*foo/bar`| => | `(get-in % [2 ::foo/bar])`|

```clojure
(#%(println :%1           %1 
            :%2           %2 
            :%1:x         %1:x
            :%:x          %:x
            :%2:*/x       %2:*/x
            :%2:foo       %2:foo
            :%3:*foo/bar  %3:*foo/bar)
 {:x 1}
 {::x 2 
  :foo 3}
 {::foo/bar 3})
; :%1 {:x 1} :%2 {:perc.core/x 2, :foo 3} :%1:x 1 :%:x 1 :%2:*/x 2 :%2:foo 3 :%3:*/foo/bar 3
```

## Return literals

You don't have to follow the `#%` tag with a set of parenthesis - you can use any collection or elide one altogether. _Return literal_ vectors and maps can be used for quick updates in place, like:

```clojure
(#%{::a (inc %1) ::b (inc %2)} 4 5)
; #:cljs.user{:a 5, :b 6}
```

Or

```clojure
(#%[(inc %:x) (inc %:y)] {:x 4 :y 5})
; [5 6]
```

Or just for wrapping stuff

```clojure
(#%{:a %1 :b %2} 4 5)
; {:a 4, :b 5}
```

This makes for a short and quick way to restructure data as it flows through deeply nested transformations.

To return a namespaced map literal, you must put a space between the `#%` reader tag and the map:

```clojure
(#% #:Point{:x %:*/x :y %:*s/y :z %:foreign/z}
   {::x 1 ::s/y 2 :foreign/z 3}) 
; #:Point{:x 1, :y 2, :z 3}
```

Otherwise Clojure would have concatenated them into a `#%#:Point` token above.

## Advanced Path Expressions

As discussed in the [overview](#overview), you can concatenate anonymous parameters together to create arbitrarily long _path expressions,_ which are like a cross between a `get-in` and the `->` thread operator.

### Path Expression Table
|Path Expression|=>|Expression|
|---:|---:|:---|
|`%:x:y`| => | `(get-in % [0 :x :y])`|
|`%2:x:y/z`| => | `(get-in % [1 :x :y/z])`|
|`%1:x:*/y/z`| => | `(get-in % [0 :x ::y/z])`|
|`%2:x:*/z`| => | `(get-in % [1 :x ::z])`|
|`%2:x:a%1:*/z`| => | `(get-in % [1 :x :a 0 ::z])`|
|`%2:x:a%1:*/z%3`| => | `(get-in % [1 :x :a 0 ::z 2])`|
|`%:x:a%1:*/z%3`| => | `(get-in % [0 :x :a 0 ::z 2])`|

```clojure
(#%(println :%:x:y          %:x:y
            :%2:x:y/z       %2:x:y/z
            :%1:x:*y/z      %1:x:*y/z
            :%2:x:*/z       %2:x:*/z
            :%:x:a%1:*/z    %:x:a%1:*/z
            :%2:x:a%1:*/z%1 %2:x:a%1:*/z%1
            :%:x:a%1:*/z%3  %:x:a%1:*/z%3)
  {:x {:y 2 :y/z 3 ::y/z 4 ::z 5 :a [{::z [6 7 8 9]}]}}
  {:x {:y 9 :y/z 8 ::y/z 7 ::z 6 :a [{::z [5 4 3 2]}]}})
; :%:x:y 2 :%2:x:y/z 8 :%1:x:*/y/z 4 :%2:x:*/z 6 :%:x:a%1:*/z [6 7 8 9] :%2:x:a%1:*/z%1 5 :%:x:a%1:*/z%3 8
```

## Mapping and Reducing

Mapping and reducing are things we do a lot in Clojure. With `perc`s we can more easily slice and dice named values in deeply nested transformations. Here's a more involved example:

```clojure
(->> [{:a {:z {:x 5}}
       :b {:c {:p {:q 6}}}}
      {:a {:z {:x 7}}
        :b {:c {:p {:q 8}}}}
      {:a {:z {:x 9}}
        :b {:c {:p {:q 10}}}}]

     (mapv #%{:a {:z {:x (inc %:a:z:x)}}
              :b {:c {:p {:q (dec %:b:c:p:q)}}}})

     (reduce #%{:a {:z {:x (+ %1:a:z:x %2:a:z:x)}}
                :b {:c {:p {:q (+ %1:b:c:p:q %2:b:c:p:q)}}}}))
```

So far, we've been operating over collections of maps. What if we're operating over a collection of vectors here? Top level index references like `%1` aren't automatically indexing into the first param like a top level keyword reference like `%:a` (otherwise we wouldn't be able to access params other than the first). In order to imply top level, indexical access into the first param, we can use the `#%1` 1-arity version of `perc`.

### 1-arity Fns
The `#%1` reader tag is for when you're mapping over collections of vectors and you know `map` will only be passing one parameter to the fn. Top level indexes within the fn body will then index into the first param (rather than the fn's whole list of params):

```clojure
(->> [[{:x 1 :y 2 :z 3} {:x 2 :y 3 :z 4} {:x 3 :y 4 :z 5}]
      [{:x 1 :y 2 :z 3} {:x 2 :y 3 :z 4} {:x 3 :y 4 :z 5}]]

      (mapv #%1[(assoc %3 :x (inc %3:x) :z (dec %3:z))
                (assoc %2 :x (inc %2:x) :z (dec %2:z))
                (assoc %1 :x (inc %1:x) :z (dec %1:z))])

      (mapv #%1[%1:x %1:y %1:z
                %2:x %2:y %2:z
                %3:x %3:y %3:z])) ; => [[4 4 4 3 3 3 2 2 2] [4 4 4 3 3 3 2 2 2]]
```

If we only used `#%` there, then in the fn body we would have had to reference the first fn param each time:

```clojure
      ...
      (mapv #%[(assoc %1%3 :x (inc %1%3:x) :z (dec %1%3:z))
      ...
      (mapv #%[%1%1:x %1%1:y %1%1:z
      ...
```

If we were passing extra arguments to the `mapv`s, then those first indexes might be useful, but usually we're not passing extra arguments.

Let's show a more involved example where we transform from vectors to maps to vectors:

```clojure
(defn rand-point-3d []
  {:tag :point-3d
   :x (rand-int 100)
   :y (rand-int 100)
   :z (rand-int 100)})

(defn rand-triangle-3d []
  [(rand-point-3d) (rand-point-3d) (rand-point-3d)])

(->> (repeatedly rand-triangle-3d)
     (take 2)
     ;; mess with the maps and attach them to keys
     (mapv #%1{:a (assoc %3 :x %3:x%inc :z %3:z%dec) ; <- notice the easter egg? teehee :D
               :b (assoc %2 :x %2:x%inc :z %2:z%dec)
               :c (assoc %1 :x %1:x%inc :z %1:z%dec)})
     ;; return them as vectors in some other sort order
     (mapv #%[:Triangle
               %:b:x %:b:y %:b:z
               %:a:x %:a:y %:a:z
               %:c:x %:c:y %:c:z]))
;; [[:Triangle 85 53 41 36 72 16 97 26 10] [:Triangle 100 15 15 99 99 24 50 97 5]]
```

## Thread Fns

_Thread functions_ allow you to create a function that is more easily threaded by a `->` operator.

Thread functions implement the `#%>` reader tag which has the same semantics as `#%1` but wraps it in a pair of parentheses so that the anonymous fn can be threaded through appropriately:

```clojure
(-> {:z/x {:y [1 'b :c 8 {::s/a {:num 9}}]}}

    #%>[%:z/x:y%5:*s/a:num]) 
; => [9]
```

It's great for quickly transforming maps within a thread context:

```clojure
(-> {::x 1 ::s/y 2 :foreign/z 3}

    #%> #:Point{:x %:*/x :y %:*s/y :z %:foreign/z}) 
; => #:Point{:x 1, :y 2, :z 3}
```

Or for condensing navigation paths until a common branch between two values in a deeply nested structure:

```clojure
(-> {:z/x {:y [1 'b :c 8 {::s/a {:num 9}}]}}

    #%> %:z/x:y

    #%>(+ %1 %5:*s/a:num)) 
; => 10
```

## Keyword arguments

[Keyword arguments](https://clojure.org/guides/destructuring#_keyword_arguments) provide a convenient way to give arguments to a function without having to worry about the order of those arguments. The official docs show this as an example:

```clojure
(defn configure [val & {:keys [debug verbose]
                        :or {debug false, verbose false}}]
  (println "val =" val " debug =" debug " verbose =" verbose))
```

Allowing us to do:

```clojure
(configure 12 :verbose true :debug true)
```

Similarly, using `perc`'s vararg syntax allows us to easily stick keyword arguments in places anonymously:

```clojure
(def app 
  {:app-name :foo-server
   :routes ["*" 200]
   :configure #%(do (when %&:debug
                      (when %&:verbose (println (str "Welcome to " %1:app-name%name "!")))
                      (println :app-state %1)
                      (println :config %2)
                      (println :optional-arguments-supplied %&))
                    #_...stuff
                    (assoc %1 :config (merge %2 %&)))})

(def config {:accept-connections true})

(-> app
    #_...
    #%>(%:configure % config :verbose true :debug true)
    #_...)
;;  Welcome to foo-server!
;;  :app-state {:app-name :foo-server, :routes [* 200], :configure #function[perc.core/fn--7923]}
;;  :config {:accept-connections true}
;;  :optional-arguments-supplied {:verbose true, :debug true}
;=> {:app-name :foo-server,
;;   :routes ["*" 200],
;;   :configure #function[perc.core/fn--7923],
;;   :config {:accept-connections true, :verbose true, :debug true}}
```

# Nesting

Like with traditional, sugared anonymous functions, you can't nest `perc`s of a given type, like:

```clojure
#%(do #%()) ; => Syntax error reading source at (REPL:xxx:xx).
            ;    No nesting for reader tag #%
```

## `%%` & `%%%`

However, there are also the tagged literals `#%%` and `#%%%` for explicitly nesting deeper levels. They each transform `%%` and `%%%` symbols, respectively, within their enclosing forms.

Suppose we had some data:

```clojure
{::demo/events [e1 e2 e3]
 :acme/event-handler (fn ...
 ::time-out-callback (fn ...
 ... }
```

Using old-school syntax, we might do something like this to apply the event handler to the events:

```clojure
(fn [{events ::demo/events
      event-handler :acme/event-handler]
  (mapv #(event-handler (:event/data %))
    events)
```

Using the new-school syntax, we don't have to give as many things new names:

```clojure
#%(mapv
    #%%(%:acme/event-handler %%:event/data)
    %:*/demo/events)
```

Being able to reference multiple levels of depth with `%`, `%%` and `%%%` allows us to maintain syntactic concision without having to take the classical `(fn [])` escape hatch as often.

# How

`perc`s employ [tagged literals](https://clojure.org/reference/reader#tagged_literals). They essentially work like macros but take only one parameter (the token to the right of them) and have no parenthesis around themselves and their parameter. At read time, the pair of reader tag and its parameter are replaced by the return value of the tag's transformation function. Because we only need to instrument a single form with our syntax sugar, tagged literals work out pretty good for this use-case.

# Why Not?

One downside is that, as with destructuring by :keys, you cannot reference by keys that are not actually keywords. So it's most useful for maps where keys are actually keywords and not strings or other types of objects. Those values can still be referenced by index, as with the existing sugared anonymous function syntax.

Clojure(Script)'s anonymous function syntax sugar is actually built into the language's reader. Because a `perc`'s anonymous function expand to a regular anonymous function, the resulting code will likely be a little larger.

Technically, the tokens within the anaphoric macros are not "valid" Clojure symbols.

`perc`s allow Clojure to use ClojureScript's more permissive anonymous function arity handling. As such, they do not assert Clojure's more strict arity checking.

# Roadmap

One thing I'd like to explore next is some kind of relational query syntax, leaning on something like [EQL](https://github.com/edn-query-language/eql), [Meander](https://github.com/noprompt/meander), [Odin](https://github.com/halgari/odin) or [juxt/pull](https://github.com/juxt/pull) under the hood.

It would be nice to have a syntax that can express both the query and the projection in a single form, but I can't figure out where to squeeze declarations for joins cleanly.

```clojure
#%?{:size ?size%:house:size|:door:size ; <- joins whole query on :size but returns value
    :number-of-windows (count %2:house-templates%?size:windows)
    :about-the-door %:door:description} ; => {:size :large 
                                        ;     :number-of-windows 20
                                        ;     :about-the-door "Big'ol door"}
```

But if we didn't want to actually return the size, we'd have to declare it outside the form body:

```clojure
#%?(do ?size%:house:size|:door:size
    {:number-of-windows (count %2:house-templates%?size:windows)
     :about-the-door %:door:description} ; => {:number-of-windows 20
                                         ;     :about-the-door "Big'ol door"}
```

Let me know if you think of anything.

From a performance perspective, I'd also like to bring in something like [structural/with-slots](https://github.com/joinr/structural#structuralcorewith-slots) to allow type hinting and auto optimization. 

Per structural's readme:
    
    It’s basically a poor man’s optimizing compiler for the use-case of unpacking type-hinted structures for efficient reads

So it'd be nice to output code that is even more efficient than what you'd normally write by hand and allow for type hints in really tight loops.

Additionally, I'd like to explore tools like [streamer](https://github.com/divs1210/streamer), where we unwind and rewind code into `->>` forms, scan for contiguous blocks of transducing candidates and then use `streamer` to rewrite those into transducing blocks, thereby preserving laziness when implied but reducing down to a transducer when eager consumption is detected - auto-transducified code. With those two additions, you might even get a non-trivial _performance_ `perc`.
