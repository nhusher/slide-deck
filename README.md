# Slides for VTCodeCamp7

If you want to run the slides project yourself, you'll need a copy of the Java 8 SDK and [Leinengen](http://leiningen.org/) on your computer. How you get those is up to you. Once you have them, you can run:

```
lein run -m clojure.main scripts/repl.clj
```

Which will boot up the figwheel app and drop you into a repl. You can now page through the slides at `http://localhost:3449`. Any changes you make to the clojurescript files will be hot-reloaded. You can do the same for CSS if you install `sass` and run `build.sh` alongside the repl.

The deck was built with the [Cursive](https://cursive-ide.com/) plugin for [IntelliJ Idea](https://www.jetbrains.com/idea/). If you want to use that environment, you can download Cursive and set up a new run target using [these instructions on the Figwheel wiki](https://github.com/bhauman/lein-figwheel/wiki/Running-figwheel-in-a-Cursive-Clojure-REPL). This will provide a somewhat better development experience.
