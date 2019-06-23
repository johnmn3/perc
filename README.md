# `#%/%()`

Bittersweet anonymous functions, _with a `perc` ;)_


## What?

Syntactically, `perc` is very similar to Clojure's anonymous function syntax `#(Point. %1 %2 %3)`

But with a _"perc"_...

```clojure
#%/%(Point. %:x %:y %:z)
```

That's right... You never realized how badly you've wanted to effortlessly grab named values passed into anonymous functions... until now!

Sure, you gotta add these three bitter characters - `%/%` - but the extra sweetness you get out of the other end is totally worth the squeeze: `%:a-very-self-describing-parameter-name`

## Getting Started

Place the following in the `:deps` map of your `deps.edn` file:

```clojure
  ...
  johnmn3/perc {:git/url "https://github.com/johnmn3/perc"
                :sha "1c7e1d63aae9b2e59087ffc7774f6520b34e4c26"}
  ...
```

If you want to testing things out _right now_, from the comfort of your own `~/home`, go ahead and drop this in your bash pipe and smoke it:

```clojure
clj -Sdeps '{:deps {johnmn3/perc {:git/url "https://github.com/johnmn3/perc" :sha "1c7e1d63aae9b2e59087ffc7774f6520b34e4c26"}}}' -m cljs.main -c perc.core -re node -r
```

Then you should be able to test things out right away:

```clojure
Cloning: https://github.com/johnmn3/perc
Checking out: https://github.com/johnmn3/perc at 1c7e1d63aae9b2e59087ffc7774f6520b34e4c26
ClojureScript 1.10.520
cljs.user=> (#%/%(println "hi" %:x) {:x 1})
hi 1
nil
```

Once a project is launched, you don't need to require anything because tagged literals work globally.

## Usage

#### Named anonymous parameters
All you have to do is prepend your function body with `#%/%`, like you would normally use just `#` for anonymous functions. Then you can use `%` and `%1` like usual, but you can also do `%:foo` or `%1:bar`.

#### Namespaced anonymous parameters

Namespaced keywords work too:

```clojure
#%/%(Point. %::x %::nearby/y %:foreign/z)
```

#### Multiple parameters

Pulling named values out of multiple different parameters works as you'd expect.

```clojure
#%/%(response %1:ctx %2:status %2:body)
```

Here we grab `:ctx` from the first parameter, `:status` from the second, and `:body` from the second.

This also makes it easier to deal with ambiguous keys coming in from multiple map sources. For example:

```clojure
#%/%(Triangle. %1:x %1:y %1:z,
               %2:x %2:y %2:z,
               %3:x %3:y %3:z)
```

We can also more easily slice and dice named values in deeply nested transformations. Here's a more involved example:

```clojure
(defn rand-point-3d []
  {:tag :point-3d
   :x (rand-int 100)
   :y (rand-int 100)
   :z (rand-int 100)})

(defn rand-triangle-3d []
  [(rand-point-3d) (rand-point-3d) (rand-point-3d)])

;; now, deep in some transformation, somewhere deep in
;;  a data structure, we do some custom transform
(->> (repeatedly rand-triangle-3d)
  (take 10)
  ;; ... many transformations later
  (mapv
    (partial apply ;; say, we want to invert it
      #%/%[[%3:z %3:y %3:x]
           [%2:z %2:y %2:x]
           [%1:z %1:y %1:x]])))
```

#### Return literals

As you may have just noticed in the example above, literal vectors and maps can be used in place of lists if we don't need to evaluate anything, like:

```clojure
cljs.user=> (#%/%{::a (inc %1) ::b (inc %2)} 4 5)
#:cljs.user{:a 5, :b 6}
```

Or

```clojure
cljs.user=> (#%/%[(inc %:x) (inc %:y)] {:x 4 :y 5})
[5 6]
```

Or

```clojure
cljs.user=> (#%/%[(inc %1) (inc %2)] 4 5)
[5 6]
```

This makes for a short and quick way to restructure data as it flows through deeply nested transformations.

## Nesting

Like with `#()`, you can't nest `perc`s, like:
```clojure
#%/%(do #%/%())
```
Doing so will throw a syntax error.

#### `%%` & `%%%`

There's also `#%/%%` and `#%/%%%` for explicitly nesting second and third levels, respectively. They each transform `%%` and `%%%` symbols within their enclosing forms.

Suppose we had some data:

```clojure
{:events [e1 e2 e3]
 :event-handler (fn [e] ...
 :time-out-callback {:default (fn [e] ...
```

Using old-school syntax, we might do something like this to apply the event handler to the events:

```clojure
#(mapv
   (fn [inner-val]
     ((:event-handler %)
      (:event-data inner-val)))
   (:events %))
```

Using the new-school syntax:

```clojure
#%/%(mapv
      #%/%%(%:event-handler %%:event-data)
      %:events)
```

#### `$` & `?`

For alternative characters and nesting, `perc` also comes with `#%/$` and `#%/?`, each with their double and triple nesting variants as well.

```clojure
#%/%(mapv
      #%/$(%:event-handler $:event-data
            #%/?(%:time-out-callback ?:default))
      %:events)
```
To do this the old-school way, we'd end up with something that looks like this:
```clojure
#(mapv
  (fn [inner-val]
    ((:event-handler %)
     (:event-data inner-val)
     (fn [inner-inner-val]
       ((:time-out-callback %)
        (:default inner-inner-val)))))
  (:events %))
```


## How

`perc` employs [tagged literals](https://clojure.org/reference/reader#tagged_literals). They essentially work like macros but take only one parameter (the token to the right them) and have no parenthesis around themselves and their parameter. At read time, the pair of reader tag and its parameter are replaced by the return value of the tag's transformation function. Because we only need to instrument a single form with our syntax sugar, tagged literals work out pretty good for this use-case.

## Why Not?

Clojure(Script)'s anonymous function syntax sugar is actually built into the language's reader. Because `perc`'s anonymous functions expand to regular anonymous functions, the resulting code will likely be a little larger.

Technically, the tokens within the anaphoric macros are not "valid" Clojure symbols.

`perc` allows Clojure to use ClojureScript's more permissive anonymous function arity handling, however it does not assert Clojure's more strict arity checking.
