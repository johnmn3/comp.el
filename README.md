# comp.el
Affective Elements for Component Inheritance

[`comp.el`](https://github.com/johnmn3/comp.el) uses [`af.fect`](https://github.com/johnmn3/af.fect) produce abstract, _Higher Order Components_ (HOCs) that can serve as concrete components or can be used to derive other HOCs. It is currently _pre-alpha_, bringing in only what is necessary to show how one can use `af.fect` to implement functional inheritance. Over time, I plan to build out the component library.

`comp.el` doesn't actually currently `require` in Reagent/Re-frame but the components it currently constructions are intended to be used in a reactive hiccup tree like Reagent's. `comp.el` currently brings in _reagent-material-ui_ from [arttuka](https://github.com/arttuka/reagent-material-ui/blob/master/example/src/example/core.cljs) and Brian Chevalier's [Radiant](https://github.com/BrianChevalier/radiant) for built-in css-in-cljs.

`af.fect` allows you to construct _affects_ that are both _higher order_ functions and _lower order_ functions, depending on how they are called, which we might describe as _dual order_ functions. This allows functions to inherit the behavior of parent affects or by mixing in affects, allowing for multiple inheritance. Therefore, the material-ui features and Radiant's css-in-cljs will likely be broken out into separate repos eventually, which you can simply mixin to your components if desired. I added them here simply to demonstrate how one might use them with affects to construct a simple application, which you can find in the [comp.el todomvc example project](https://github.com/johnmn3/comp.el/tree/main/ex) directory, derived from Re-frame's example todomvc project.

The obligitory todomvc screenshot: <img width="400" alt="Screen Shot 2022-06-20 at 2 07 40 PM" src="https://user-images.githubusercontent.com/127271/174676451-fff0dfa6-6479-4763-b3f4-285c37286830.png">

There's a tiny bit of CSS in the index.html file, to get the html and body styled, but the rest is all in CLJS. Radiant allows you to express CSS media queries, pseudo-selectors and animation keyframes all in-line - pretty awesome!

Todomvc isn't actually a large enough application to necessitate HOCs and functional inheritance. My desire to create `af.fect` originally came from building fairly large frontends in Re-frame and finding a proliferation of concrete implementations of similar-but-slightly-different components. I started to get annoyed by how similar these large, complex components were and began thinking about how I might go about abstracting out their commonalities.

These more complex frontends have complex validation and state management. `af.fect` was borne out of that need to alleviate the burden of these concretions in large frontends. `comp.el` is in the very beginning stages of producing a frontend library that leverages `af.fect` to solve that abstraction problem for the frontend. I'll be porting some old form-validation and state management code to this newer, public version of `af.fect` and `comp.el` soon, to demonstrate how this _affect oriented_ paradigm can help developers abstract these lower-level concerns out of their application code and their hair.

In the mean time, feel free to reach out and discuss the project on [clojure-verse placeholder post], file an issue here or contribute to the future of `comp.el`, `af.fect` and this affective programming paradigm.

See [af.fect](https://github.com/johnmn3/af.fect) for mor info.
