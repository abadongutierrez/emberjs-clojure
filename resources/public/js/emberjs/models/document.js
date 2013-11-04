Documents.Document =  DS.Model.extend({
	title: DS.attr('string'),
	text: DS.attr('string')
});

Documents.Document.FIXTURES = [
{
	id: 1,
	title: "Document 1",
	text: "bla bla bla bla"
},
{
	id: 2,
	title: "Document 2",
	text: "bla bla bla bla"
}
];