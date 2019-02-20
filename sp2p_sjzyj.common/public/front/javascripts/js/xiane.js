
/*银行限额*/
    function ccc(selp,poin,jour) {
        var option=selp.text()
        if (!(option.indexOf('建设银行')==-1)){
            poin.text('5')
            jour.text('100')
        }else if(!(option.indexOf('农业银行')==-1)){
        	poin.text('20')
            jour.text('50')
        }else if(!(option.indexOf('工商银行')==-1)){
        	poin.text('5')
            jour.text('5')
        }else if(!(option.indexOf('光大银行')==-1)){
        	poin.text('20')
            jour.text('100')
        }else if(!(option.indexOf('兴业银行')==-1)){
        	poin.text('5')
            jour.text('5')
        }else if(!(option.indexOf('中国银行')==-1)){
        	poin.text('5')
            jour.text('50')
        }else if(!(option.indexOf('交通银行')==-1)){
        	poin.text('5')
            jour.text('100')
        }else if(!(option.indexOf('中信银行')==-1)){
        	poin.text('0.5')
            jour.text('0.5')
        }else if(!(option.indexOf('平安银行')==-1)){
        	poin.text('10')
            jour.text('100')
        }else if(!(option.indexOf('广发银行')==-1)){
        	poin.text('20')
            jour.text('100')
        }else if(!(option.indexOf('民生银行')==-1)){
        	poin.text('20')
            jour.text('100')
        }else if(!(option.indexOf('北京银行')==-1)){
        	poin.text('0.5')
            jour.text('100')
        }else if(!(option.indexOf('上海银行')==-1)){
        	poin.text('0.5')
            jour.text('0.5')
        }else if(!(option.indexOf('福建银行')==-1)){
        	poin.text('0.5')
            jour.text('0.5')
        }else if(!(option.indexOf('江苏银行')==-1)){
        	poin.text('0.5')
            jour.text('2')
        }else if(!(option.indexOf('南京银行')==-1)){
        	poin.text('0.2')
            jour.text('5')
        }else if(!(option.indexOf('青海银行')==-1)){
        	poin.text('10')
            jour.text('50')
        }else if(!(option.indexOf('宜宾银行')==-1)){
        	poin.text('10')
            jour.text('50')
        }else if(!(option.indexOf('郑州银行')==-1)){
        	poin.text('2')
            jour.text('5')
        }
        
    }
    ccc($('#selP option:selected'),$('#poin'),$('#jour'));
    $('#selP').change(function () {
        ccc($('#selP option:selected'),$('#poin'),$('#jour'))
    })
    ccc($('#sellist option:selected'),$('#a'),$('#b'));
    $('#sellist').change(function () {
        ccc($('#sellist option:selected'),$('#a'),$('#b'))
    })
