/*单位工程*/
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.ProjectBitWin', {
		extend : 'Ext.window.Window',
		initComponent : function() {
          	var me = this;
  			Ext.apply(this, {
  				width : "99%",
				height : "99%",
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
                    title: '分部分项',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.ProjectBitPanel", {
                    		bitProjectId:me.bitProjectId
                    	})
                    ],
                    itemId: 'bitproject'  
                },{  
                    title: '运材安汇总',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.YCATotal", {
                    		bitProjectId:me.bitProjectId
                    	})
                    ],
                    itemId: 'ycatotal'
                }, {  
                    title: '费用汇总',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.FeeTotal", {
                    		bitProjectId:me.bitProjectId
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
  			
  			
  			this.on('tabChange', function(tabPanel, newCard, oldCard, eOpts){
  				
  				//运财安汇总
  				if("ycatotal" == newCard.itemId){
  					Ext.getCmp('ycatotal-costvaluegrid').getStore().loadPage(1);
  				}
  				//分部分项
  				if("bitproject" == newCard.itemId){
  					Ext.getCmp('projectbitpanel-bititemgrid').getStore().load();
  					Ext.getCmp('projectbitpanel-bitdetailgrid').getStore().removeAll();
  				}
  				
  				//费用汇总
  				if("costtotal" == newCard.itemId){
  					Ext.getCmp('feetotal-feevaluegrid').getStore().loadPage(1);
  					Ext.getCmp('feetotal-feedetailgrid').getStore().loadPage(1);
  				}
  				
  			})
  			
			this.callParent(arguments);
		}
	});

});