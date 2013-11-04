//App.ApplicationController = Ember.Controller.extend();

Documents.DocumentsController = Ember.ArrayController.extend({
	actions: {
		createDocument: function() {
			console.log('### -> createDocument() ###');
			var title = this.get('newTitle');
			var text =  this.get('newText');

			if (title == undefined ||  text == undefined) { return; }
			if (!title.trim() || !text.trim()) { return; }

			var newDoc = this.store.createRecord('document', {
				title: title,
				text: text
			});

			this.set('newTitle', '');
			this.set('newText', '');

			newDoc.save();
		}
	}
});