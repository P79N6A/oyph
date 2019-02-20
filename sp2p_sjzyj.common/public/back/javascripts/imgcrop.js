//排序
define(['jcrop'],function(){
	function crop(size){
		var cropImg = document.getElementById('cropimg'),
    	cropWidth = cropImg.width,
    	cropHeight = cropImg.height;
			// Create variables (in this scope) to hold the API and image size
		    var jcrop_api,
		        boundx,
		        boundy,

		        // Grab some information about the preview pane
		        $preview = $('.crop-preview'),
		        $pcnt = $('.crop-preview .preview-container'),
		        $pimg = $('.crop-preview .preview-container img'),
		        xsize,
		        ysize;
		    var cropSize = [];
		    cropSize[0] = size ? size[0] : cropWidth;
		    cropSize[1] = size ? size[1] : cropHeight;
	        var wth = cropWidth/cropHeight,
	        	sizeto = cropSize[0] / cropSize[1];
	        if(sizeto > 1){
		        xsize = $pcnt.width();
		        ysize = xsize / sizeto;
	        	$pcnt.height(ysize);
	        }else{
	        	ysize = $pcnt.height();
	        	xsize = ysize * sizeto;
	        	$pcnt.width(xsize);
	        }
		    $(cropImg).Jcrop({
		      onChange: updatePreview,
		      onSelect: updatePreview,
		      minSelect: [20,20],
		      boxWidth: function(){
		      	if(wth >= 4/3){
		      		return 640;
		      	}else{
		      		return 'auto';
		      	}
		      }(),
		      boxHeight: function(){
		      	if(wth < 4/3){
		      		return 480;
		      	}else{
		      		return 'auto';
		      	}
		      }(),
		      aspectRatio: size ? sizeto : 0
		    },function(){
		      // Use the API to get the real image size
		      var bounds = this.getBounds();
		      boundx = bounds[0];
		      boundy = bounds[1];
		      // Store the API in the jcrop_api variable
		      jcrop_api = this;
		      var holderBox = $('.jcrop-holder'),
		      	  boxWidth = holderBox.width(),
		      	  boxHeight = holderBox.height();
		      holderBox.css({
		      	'left': (640-boxWidth)/2,
		      	'top': (480-boxHeight)/2
		      });
			    $('body').on('click','#maxSize',function(){
		    		jcrop_api.setOptions({
		    			setSelect: [0,0,boundx,boundy]
		    		});
			    }).on('click','#standardSize',function(){
			    	jcrop_api.setOptions({
		    			setSelect: [0, 0 , cropSize[0], cropSize[1]]
		    		});
			    });
		    });
		    $('.crop-dialog').find('.dialog-console,.dialog-close').click(function(){
		    	jcrop_api.destroy();
		    	cropImg.src = '';
		    	cropImg.style.cssText = '';
		    });
		    function updatePreview(c)
		    {
		      if (parseInt(c.w) > 0)
		      {
		        var rx = xsize / c.w;
		        var ry = ysize / c.h;
		        $pimg.css({
		          width: Math.round(rx * boundx) + 'px',
		          height: Math.round(ry * boundy) + 'px',
		          marginLeft: '-' + Math.round(rx * c.x) + 'px',
		          marginTop: '-' + Math.round(ry * c.y) + 'px'
		        });
		      }
		      $('#x').val(c.x);
		      $('#y').val(c.y);
		      $('#w').val(c.w);
		      $('#h').val(c.h);
		    };
	    }
	return crop;
});
