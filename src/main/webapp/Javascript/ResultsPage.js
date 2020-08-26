var imageFrame = {
	currentPhotoId: "",
	currentPhoto: "",
	displayingImage: document.getElementById("image"),
	leftArrow: document.getElementById("leftArrow"),
	leftPageArrow: document.getElementById("leftPageArrow"),
	rightArrow: document.getElementById("rightArrow"),
	rightPageArrow: document.getElementById("rightPageArrow"),

	//The syntax for member functions is for IE compatibility
	/**
	 * Shows the fullsize pane and loads the image corresponding to the UUID arg.
	 * Runs when the user taps on a thumbnail
	 * @param {type} UUID
	 * @returns {undefined}
	 */
	showFullsize: function(UUID) {
		scroll.freeze();
		this.currentPhotoId = UUID;
		this.currentPhoto = document.getElementById(UUID);
		pageContext.frameHolder.style.visibility = "visible";
		this.updateContents();
		pageContext.frameHolder.style.opacity = 1;
	},

	/*The 7 next functions are referenced by various user controls*/
	frameClose: function() {
		scroll.unfreeze();
		pageContext.frameHolder.style.opacity = 0;
		//wait for animation to finish
		pageContext.frameHolder.addEventListener('transitionend', hide );
		
		function hide() {
			pageContext.frameHolder.style.visibility = "hidden";
			pageContext.frameHolder.removeEventListener('transitionend', hide);
		}
		this.currentPhotoId = "";
		this.currentPhoto = "";
		this.leftPageArrow.style.display = "none";
		this.rightPageArrow.style.display = "none";
		this.updateURL();
	},
	navigateFullPage: function() {
		window.location.href = "/FamilyPhotos/View?UUID=" + this.currentPhotoId;
	},
	previousPhoto: function() {
		var previousPhoto = this.currentPhoto.previousElementSibling;
		this.currentPhotoId = previousPhoto.id;
		this.currentPhoto = document.getElementById(this.currentPhotoId);
		this.updateContents();
	},
	nextPhoto: function() {
		var nextPhoto = this.currentPhoto.nextElementSibling;
		this.currentPhotoId = nextPhoto.id;
		this.currentPhoto = document.getElementById(this.currentPhotoId);

		this.updateContents();
	},
	navigatePreviousPage: function() {
		var newUrl = new Url(pageContext.previousPage);
		newUrl.query.showImage = "last";
		history.pushState({}, "", newUrl.toString());
		document.location.reload();
	},
	navigateNextPage: function() {
		var newUrl = new Url(document.getElementById("next").href);
		newUrl.query.showImage = "first";
		history.pushState({}, "", newUrl.toString());
		document.location.reload();
	},
	/**
	 * Loads the photo and its corresponding data from the server into the
	 * client. Knows which photo to load from currentPhotoId.
	 * @returns {undefined}
	 */
	updateContents: function() {
		this.displayingImage.style.display = "none";
		pageContext.infoPane.style.display = "none";
		this.showLoading();
		this.hideError();

		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function () {
			if (this.readyState === 4 && this.status === 200) {
				try {
					var photoData = JSON.parse(this.responseText);
				} catch (err) {
					imageFrame.showError('Error: you appear to be logged out.');
					return;
				}
				imageFrame.displayingImage.onclick = function () {
					window.location.href = photoData.photoPath;
				};
				addPhotoInfoToPane(photoData.photoPath, "path");
				addPhotoInfoToPane(photoData.tags, "tags");
				addPhotoInfoToPane(photoData.comment, "comment");
				addPhotoInfoToPane(photoData.decade, "decade");
				addPhotoInfoToPane(photoData.date, "date");
				pageContext.infoPane.style.display = "block";
				imageFrame.checkArrows();

				imageFrame.displayingImage.src = photoData.photoPath;
				imageFrame.displayingImage.onload = function () {
					imageFrame.displayingImage.style.display = "block";
					imageFrame.hideLoading();
				};
				imageFrame.displayingImage.onerror = function () {
					imageFrame.showError("Image could not load.");
				};
			} else if (this.status >= 400) {
				imageFrame.showError("The image id appears to be wrong.");
			}
		};
		xhttp.open("GET", "/FamilyPhotos/View?UUID=" + this.currentPhotoId + "&Json=true", true);
		xhttp.send();

		this.updateURL();
		
		//inner helper function
		function addPhotoInfoToPane(photoElement, idString) {
			if (photoElement === undefined || photoElement === null || photoElement === '') {
				document.getElementById(idString).style.display = "none";
			} else {
				document.getElementById(idString).style.display = "block";
				document.getElementById(idString).childNodes[1].data = photoElement;
			}
		}
	},
	/**
	 * Shows/hides the two different kinds of arrows on the fullsize pane
	 * based on whether there is another photo in this page to show and
	 * whether a previous/next page exists
	 * @returns {undefined}
	 */
	checkArrows: function() {
		if (this.currentPhoto === null) {
			var previousPhotoOnPageExists = false;
			var nextPhotoOnPageExists = false;
			var previousPageExists = false;
			var nextPageExists = false;
		} else {
			var previousPhotoOnPageExists = this.currentPhoto.previousElementSibling !== null;
			var nextPhotoOnPageExists = this.currentPhoto.nextElementSibling !== null;
			var previousPageExists = pageContext.previousPage !== "javascript:;";
			var nextPageExists = pageContext.nextPage !== "javascript:;";
		}

		if (previousPhotoOnPageExists) {
			this.leftArrow.style.display = "block";
			this.leftPageArrow.style.display = "none";
		} else {
			this.leftArrow.style.display = "none";
			if (previousPageExists) {
				this.leftPageArrow.style.display = "block";
			} else {
				this.leftPageArrow.style.display = "none";
			}
		}


		if (nextPhotoOnPageExists) {
			this.rightArrow.style.display = "block";
			this.rightPageArrow.style.display = "none";
		} else {
			this.rightArrow.style.display = "none";
			if (nextPageExists) {
				this.rightPageArrow.style.display = "block";
			} else {
				this.rightPageArrow.style.display = "none";
			}
		}
	},
	
	/*small utility functions*/
	showLoading: function() {
		document.getElementById("loading").style.display = "block";
	},
	hideLoading: function() {
		document.getElementById("loading").style.display = "none";
	},
	showError: function(errorMessage) {
		this.hideLoading();
		document.getElementById("error").style.display = "block";
		document.getElementById("error").innerHTML = errorMessage;
	},
	hideError: function() {
		document.getElementById("error").style.display = "none";
	},
	updateURL: function() {
		var currentURL = new Url;
		currentURL.query.showImage = this.currentPhotoId;
		history.replaceState({}, "", currentURL);
	}
};

var pageContext = {
	frameHolder: document.getElementById("frameHolder"),
	infoPane: document.getElementById("infoPane"),
	previousPage: document.getElementById("previous").href,
	nextPage: document.getElementById("next").href
};

var addToCollection = {
	overlay : document.getElementById('overlay'),
	htmlElement: document.getElementById('addToCollection'),
	photoIdContainer: document.getElementById('photoIDs'),
	unfreezeOnExit: true,
	/**
	 * Displays the addToCollection pane. Adds the hidden elements that 
	 * correspond to the photoIDs given.
	 * @param {type} photoIDs
	 * @returns {undefined}
	 */
	open: function (photoIDs) {
		if (photoIDs.length === undefined || photoIDs.length < 1) {
			alert ('No photos selected!');
			return;
		}
		
		this.overlay.style.display = "block";
		if (scroll.frozen === true) {
			this.unfreezeOnExit = false;
		} else {
			scroll.freeze();
			this.unfreezeOnExit = true;
		}
		this.htmlElement.style.display = "block";
		
		document.getElementById('numberPhotos').innerHTML = photoIDs.length;
		
		//add photoIDs to the form
		for (i = 0; i < photoIDs.length; i++) {
			var imgElement = document.createElement('input');
			imgElement.setAttribute('type', 'hidden');
			imgElement.setAttribute('value', photoIDs[i]);
			imgElement.setAttribute('name', 'photoIDs');
			this.photoIdContainer.appendChild(imgElement);
		}
		//get the collection names
		var xhr = new XMLHttpRequest();
		xhr.onreadystatechange = function() {
			if (this.readyState === 4 && this.status === 200) {
				var collections;
				try {
					collections = JSON.parse(this.responseText);
				} catch (err) {
					alert('you appear to be logged out. Try refreshing');
					return;
				}
				var collectionRadioButtons = document.getElementById('collectionRadioButtons');
				for (i = 0; i < collections.length; i++) {					
					var radioButton = document.createElement('input');
					radioButton.setAttribute('type', 'radio');
					radioButton.setAttribute('value', collections[i].collectionName);
					radioButton.setAttribute('name', 'collectionName');
					radioButton.setAttribute('id', collections[i].collectionName);
					collectionRadioButtons.appendChild(radioButton);
					
					var label = document.createElement('label');
					label.setAttribute('for', collections[i].collectionName);
					label.innerHTML = collections[i].collectionName;
					collectionRadioButtons.appendChild(label);
					
					collectionRadioButtons.appendChild(document.createElement("BR"));
				}
			}
		};
		xhr.open("GET", "/FamilyPhotos/Collections?action=getCollections", true);
		xhr.send();
	},
	/**
	 * Finds all photos in the page with a checked box
	 * @returns {Array|addToCollection.checkedPhotos.photoIDs}
	 */
	checkedPhotos: function () {
		var thumbnails = document.getElementById('thumbnailContainer');
		var photoIDs = [];
		for (i = 0; i < thumbnails.children.length; ++i) {
			var checkbox = thumbnails.children[i].querySelector("input");
			if (checkbox.checked) {
				photoIDs.push(thumbnails.children[i].id);
			}
		}
		return photoIDs;
	},
	/**
	 * Closes the addToCollection
	 * @returns {undefined}
	 */
	close: function () {
		if (this.unfreezeOnExit) {
			scroll.unfreeze();
		}
		this.overlay.style.display = "none";
		this.htmlElement.style.display = "none";
		this.photoIdContainer.textContent = '';//clear the photo ids
		document.getElementById('collectionRadioButtons').textContent = ''//remove radio buttons;
		this.htmlElement.reset();//reset form
		document.getElementById('response').src = "/FamilyPhotos/MakeSelection.html";//clear response frame
		this.uncheckAll();
	},
	/**
	 * Checks all photos on the page
	 * @returns {undefined}
	 */
	checkAll: function() {
		var thumbnails = document.getElementById('thumbnailContainer');
		for (i = 0; i < thumbnails.children.length; ++i) {
			thumbnails.children[i].classList.add("checked");
			var checkbox = thumbnails.children[i].querySelector("input");
			checkbox.checked = true;
		}
	},
	/**
	 * Unchecks all photos on the page
	 * @returns {undefined}
	 */
	uncheckAll: function() {
		var thumbnails = document.getElementById('thumbnailContainer');
		for (i = 0; i < thumbnails.children.length; ++i) {
			thumbnails.children[i].classList.remove("checked");
			var checkbox = thumbnails.children[i].querySelector("input");
			checkbox.checked = false;
		}
	}
};


loadFromUrl();
/**
 * Checks if the URL defines an image to show fullsize and show it if so
 * @returns {undefined}
 */
function loadFromUrl() {
	var currentUrl = new Url;
	var hash = currentUrl.query.showImage;
	if (hash === undefined || hash.length < 2)
		return;
	if (hash === "first") {
		imageFrame.showFullsize(document.getElementById('thumbnailContainer').firstElementChild.id);
	} else if (hash === "last") {
		imageFrame.showFullsize(document.getElementById('thumbnailContainer').lastElementChild.id);
	} else {
		imageFrame.showFullsize(hash);
	}
}