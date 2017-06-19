/*单位工程子目选择*/
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.ProjectItemWin', {
		extend : 'Ext.window.Window',
		title:"查询",
		initComponent : function() {
          	var me = this;
  			Ext.apply(this, {
  				width : "50%",
				height : "50%",
				bodyPadding : '5 5',
				modal : true,
				layout : 'fit',
				items : [Ext.create("Budget.app.bussinessProcess.ProjectBitWin.TabPanel", {
						bitProjectId: me.bitProjectId
					})
				]
  			});
  			this.callParent(arguments);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.ProjectBitWin.TabPanel', {
		extend : 'Ext.tab.Panel',
        initComponent : function() {
          	var me = this;
  			Ext.apply(this, {
  				layout : 'border',
  				items:[{  
                    title: '定额',  
                    items:[
                    	
                    ],
                    itemId: 'dinge'  
                },{  
                    title: '运材安',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.ProjectYCATotalPanel", {
                    		
                    	})
                    ],
                    itemId: 'yca'
                }]
  			});
        	
			this.callParent(arguments);
		}
	});

});