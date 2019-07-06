# `#%/%()`

Bittersweet anonymous functions, _with a `perc` ;)_


## What?

Syntactically, `perc`s are very similar to Clojure's anonymous function syntax `#(Point. %1 %2 %3)`

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

If you want to test things out _right now_, from the comfort of your own `~/home`, go ahead and drop this in your bash pipe and smoke it:

```clojure
clj -Sdeps '{:deps {johnmn3/perc {:git/url "https://github.com/johnmn3/perc" :sha "1c7e1d63aae9b2e59087ffc7774f6520b34e4c26"}}}' -m cljs.main -c perc.alpha.core -re node -r
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
All you have to do is prepend your function body with `#%/%` - like you would normally use just `#` for with anonymous functions. Then you can use `%` and `%1` like usual, but you can also do `%:foo` or `%1:bar`.

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

Here we grab `:ctx` from the first parameter, `:status` from the second and `:body` from the second.

This also makes it easier to deal with ambiguous keys coming in from multiple map sources. For example, suppose we want a function that takes three point maps and provides them to a `Triangle` constructor:

```clojure
#%/%(Triangle. %1:x %1:y %1:z,
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

It doesn't get much more concise than the `perc` syntax above - at least, if you're destructuring over multiple maps only one level deep.

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
    (partial apply ;; say, we want flip and update the vertices
      #%/%[(assoc %3 :x (inc %3:x) :z (dec %3:z))
           (assoc %2 :x (dec %2:x) :z (inc %2:z))
           (assoc %1 :x (inc %1:x) :z (dec %1:z))]))
  ;; ... and more ...
  (mapv
    (partial apply
      #%/%(Triangle. %1:x %1:y %1:z,
                     %2:x %2:y %2:z,
                     %3:x %3:y %3:z))))
```

#### Return literals

As you may have just noticed in the example above, literal vectors and maps can be used for quick updates in place, like:

```clojure
cljs.user=> (#%/%{::a (inc %1) ::b (inc %2)} 4 5)
#:cljs.user{:a 5, :b 6}
```

Or

```clojure
cljs.user=> (#%/%[(inc %:x) (inc %:y)] {:x 4 :y 5})
[5 6]
```

Or just for wrapping stuff

```clojure
cljs.user=> (#%/%{:a %1 :b %2} 4 5)
{:a 4, :b 5}
```

This makes for a short and quick way to restructure data as it flows through deeply nested transformations.

#### Keyword arguments

[Keyword arguments](https://clojure.org/guides/destructuring#_keyword_arguments) provide a convenient way to arguments to a function without having to worry about the order of those arguments. The official docs show this as an example:

```clojure
(defn configure [val & {:keys [debug verbose]
                        :or {debug false, verbose false}}]
  (println "val =" val " debug =" debug " verbose =" verbose))
```

Allowing us to do:

```clojure
(configure 12 :verbose true :debug true)
```

Using `perc`'s vararg syntax allows you can easily stick keyword arguments in places anonymously:

```clojure
{...
 :configure #%/%(println "val =" %1
                  " debug =" (or %&:debug false)
                  " verbose =" (or %&:verbose false))
 ...}
```

## Nesting

Like with traditional, sugared anonymous functions, you can't nest `perc`s of a given type, like:

```clojure
#%/%(do #%/%())
```

Doing so will throw a syntax error.

#### `%%` & `%%%`

However, there are also the tagged literals `#%/%%` and `#%/%%%` for explicitly nesting deeper levels. They each transform `%%` and `%%%` symbols, respectively, within their enclosing forms.

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
  (mapv %(event-handler (:event/data %))
    events)
```

Using the new-school syntax, we don't have to give as many things new names:

```clojure
#%/%(mapv
      #%/%%(%:acme/event-handler %%:event/data)
      %::demo/events)
```

Being able to reference multiple levels of depth with `%`, `%%` and `%%%` allows us to maintain syntactic concision without having to take the classical `(fn [])` escape hatch as often. But wait, there's more...

#### `$` & `?`

For alternatives to `#%/%`, there are also `perc`s for `#%/$` and `#%/?`, each with their double and triple nesting variants as well.

```clojure
#%/%(mapv
      #%/$(%:acme/event-handler $:event/id $:event/data
            #%/?(%::time-out-callback ?:error/id ?:error/time-out-msg))
      %::demo/events)
```
To do this the old-school way, we'd end up with something that looks like this:
```clojure
(fn [{events ::demo/events
      time-out-callback ::time-out-callback
      event-handler :acme/event-handler]
  (mapv
    (fn [{:keys [event/data event/id]}]
      (event-handler id data
       (fn [{error-id :error/id msg :error/time-out-msg}]
         (time-out-callback error-id msg))))
    events)
```

## How

`perc`s employ [tagged literals](https://clojure.org/reference/reader#tagged_literals). They essentially work like macros but take only one parameter (the token to the right of them) and have no parenthesis around themselves and their parameter. At read time, the pair of reader tag and its parameter are replaced by the return value of the tag's transformation function. Because we only need to instrument a single form with our syntax sugar, tagged literals work out pretty good for this use-case.

## Why Not?

Clojure(Script)'s anonymous function syntax sugar is actually built into the language's reader. Because a `perc`'s anonymous function expand to a regular anonymous function, the resulting code will likely be a little larger.

Technically, the tokens within the anaphoric macros are not "valid" Clojure symbols.

`perc`s allow Clojure to use ClojureScript's more permissive anonymous function arity handling, however they do not assert Clojure's more strict arity checking.
