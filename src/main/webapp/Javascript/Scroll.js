/**
 * Object with functions to freeze and unfreeze background scrolling
 * @type type
 */
var scroll = {
	scrollTop: 0,
	frozen: false,
	navbar: document.getElementsByTagName('HEADER'),
	freeze: function() {
		var scrollTemp;
		scrollTop = window.scrollY;
		//accounts for the position:sticky navbar disappearing in firefox
		if (this.navbar !== undefined && this.navbar.length > 0) {
			this.navbar[0].style.position = 'fixed';
			this.navbar[0].style.left = 0;
			scrollTemp = -scrollTop + this.navbar[0].offsetHeight;
		} else {
			scrollTemp = -scrollTop;
		}
		var element = document.getElementsByTagName('BODY')[0];
		element.style.position = 'fixed';
		element.style.width = '100%';
		element.style.top = scrollTemp + 'px';	
		
		this.frozen = true;
	},
	unfreeze: function () {		
		if (this.navbar !== undefined && this.navbar.length > 0) {
			this.navbar[0].style.position = "static";
			this.navbar[0].style.position = 'sticky';
			this.navbar[0].style.position = '-webkit-sticky';
		}
		
		var body = document.getElementsByTagName('BODY')[0];
		body.style.position = 'static';
		window.scrollTo(0, scrollTop);
		
		this.frozen = false;
	}
};