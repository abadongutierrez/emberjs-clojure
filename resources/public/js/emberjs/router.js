/*App.Router = Ember.Router.extend({
	root: Ember.Route.extend({
		index: Ember.Route.extend({
			route: '/'
		})
	})
});*/

Documents.Router.map(function() {
	this.resource('documents', {
		path: '/'
	});
});

// By convention:
// default template: 'documents'
// ArrayController named 'DocumentsController'
Documents.DocumentsRoute = Ember.Route.extend({
  model: function () {
    return this.store.find('document');
  }
});