# scribe

Remote message logging API.

## Find a grain of salt and take this with it.

This is more or less my first experiment into non-trivial Clojure. There
are no tests and it's probably as un-idiomatic Clojure as you can find.
Deal with it.

## API
```
POST   /logs
GET    /logs/:uuid
DELETE /logs/:uuid
GET    /logs/:uuid/messages
POST   /logs/:uuid/messages {"content": "IM A LOG MESSAGE"}
```

## Credits

- @cldwalker for slowing tempting me into madness and useful code for reference.
