//费用汇总
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.ProjectCostTotalPanel', {
		extend : 'Ext.panel.Panel',
		width: "100%",  
		initComponent : function() {
			var me = this;
			Ext.define('User', {  
		        extend: 'Ext.data.Model',  
		        fields: [  
		            {name: 'field',  type: 'string'},  
		            {name: 'field_A',   type: 'string'},  
		            {name: 'field_B', type: 'string'},  
		        ]  
		    });  

			var store = Ext.create('Ext.data.TreeStore', {
				model : "User",
				root : {
					expanded : true,
					children : [ {
						text : "detention",
						field : "field",
						field_A : "a",
						field_B : "b",
						leaf : true,
						expanded : true
					}, {
						text : "homework",
						field : "field",
						field_A : "a",
						field_B : "b",
						leaf : true
					}, {
						text : "buy lottery tickets",
						field : "field",
						field_A : "a",
						field_B : "b",
						leaf : true
					} ]
				}
			});  
		  
		   var tree = Ext.create('Ext.tree.Panel', {  
		        title: 'Simple Tree',  
		        store: store,  
		        rootVisible: true,  
		        plugins:[{
		  	        ptype: 'cellediting',
		  	        clicksToEdit: 1,
		  	        editing:false, 
		  	        listeners:{
		  	        	edit:function(editor , context , eOpts){
		  	        	}
		  	        }
		  	    }],
		        columns: {  
		            items: [{  
		                xtype:"treecolumn",  
		                text: "columnOne",  
		                dataIndex:"field",  
		            },{  
		                text: "Column A",  
		                dataIndex: "field_A",  
		                editor: {
							xtype:'triggerfield',
							triggerCls: Ext.baseCSSPrefix + 'form-search-trigger',
							editable:false,
							onTriggerClick:function(){
								Ext.create("Budget.app.bussinessProcess.ProjectItemWin", {
									bitItemGrid:Ext.getCmp("projectbitpanel-bititemgrid"),
									bitProjectId:me.bitProjectId
								}).show();
							}
						}
		            },{  
		                text: "Column B",  
		                dataIndex: "field_B",  
		            }],  
		            defaults: {  
		                flex: 1  
		            }  
		        },  
		    });  
			
			Ext.apply(this, {
				layout : 'fit',
				height:500,
				items : [tree]
			});
			this.callParent(arguments);
		}
	});
    
});