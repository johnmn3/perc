# `#%/%()`

Bittersweet anonymous functions, _with a `perc` ;)_


## What?

Syntactically, `perc` is very similar to Clojure's anonymous function syntax `#(3DPoint. %1 %2 %3)`

But with a _"perc"_... `#%/%(3DPoint. %:x %:y %:z)`

That's right... You never realized how badly you've wanted to effortlessly grab named values passed into anonymous functions... until now!

Sure, you gotta add these three bitter characters - `%/%` - but the extra sweetness you get out of the other end is totally worth the squeeze: `%:a-very-self-describing-parameter-name`

## Usage

#### Named anonymous parameters
All you have to do is prepend your function body with `#%/%`, like you would normally use just `#` for anonymous functions. Then you can use `%` and `%1` like usual, but you can also do `%:foo` or `%1:bar`.

#### Namespaced anonymous parameters
Namespaced keywords work too `#%/%(3DPoint. %::x %::nearby/y %:foreign/z)`.

#### Multiple parameters
Pulling named values out of multiple different parameters works as you'd expect.

`#%/%(response %1:ctx %2:status %2:body)` Here we grab `:ctx` from the first parameter, `:status` from the second, and `:body` from the second.

## Nesting

Like with `#()`, you can't nest `perc`s, like `#%/%(do #%/%())`. Doing so will throw a syntax error.

#### `%%` & `%%%`
There's also `#%/%%` and `#%/%%%` for explicitly nesting second and third levels, respectively. They each transform `%%` and `%%%` symbols within their enclosing forms.

#### `$` & `?`
For alternative characters and nesting, `perc` also comes with `#%/$` and `#%/?`, each with their double and triple nesting variants as well.

## How

`perc` employs [tagged literals](https://clojure.org/reference/reader#tagged_literals). They essentially work like macros but take only one parameter (the token to the right them) and have no parenthesis around themselves and their parameter. At read time, the pair of reader tag and its parameter are replaced by the return value of the tag's transformation function. Because we only need to instrument a single form with our syntax sugar, tagged literals work out pretty good for this use-case.

## Why Not?

Clojure(Script)'s anonymous function syntax sugar is actually built into the language's reader. Because `perc`'s anonymous functions expand to regular anonymous functions, the resulting code will likely be a little larger.

Technically, the tokens within the anaphoric macros are not "valid" Clojure symbols.

`perc` allows Clojure to use ClojureScript's more permissive anonymous function arity handling, however it does not assert Clojure's more strict arity checking.
