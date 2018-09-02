# re-spec

A [re-frame](https://github.com/Day8/re-frame) application designed to show how to implement forms using `clojure.spec`.

Based on [Kirill Ishanov](https://github.com/kishanov)'s [re-frame-spec-forms-example](https://github.com/kishanov/re-frame-spec-forms-example). The original code uses `clojure.spec` for validation only - the form component is still hand written so it matches the spec'ed data structure. `re-spec` tries to render the form from the spec automatically.


## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
