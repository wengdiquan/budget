/*单位工程*/
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.ProjectBitWin', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				width : "99%",
				height : "95%",
				bodyPadding : '5 5',
				modal : true,
				layout : 'fit',
				items : [Ext.create("Budget.app.bussinessProcess.ProjectBitWin.TabPanel", {
				
					})
				]
			});
			Budget.app.bussinessProcess.ProjectBitWin.superclass.constructor.call(this, config);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.ProjectBitWin.TabPanel', {
		extend : 'Ext.tab.Panel',
        initComponent : function() {
          	var me = this;
  			Ext.apply(this, {
  				layout : 'border',
  				items:[{  
                    title: '分部分项',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.ProjectBitPanel", {
                    		
                    	})
                    ],
                    itemId: 'bitproject'  
                },{  
                    title: '运材安汇总',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.ProjectYCATotalPanel", {
                    		
                    	})
                    ],
                    itemId: 'ycatotal'
                }, {  
                    title: '费用汇总',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.ProjectCostTotalPanel", {
                    		
                    	})
                    ], 
                    itemId: 'costtotal'  
                },
                {  
                    title: '报表',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.ProjectReportPanel", {
                    		
                    	})
                    ], 
                    itemId: 'report'  
                }]
  			});
        	
			this.callParent(arguments);
		}
	});

});