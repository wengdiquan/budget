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
		            {name: 'code',  type: 'string'},  
		            {name: 'name',   type: 'string'},
		            {
						name : 'content',
						type : 'double'
					}, {
						name : 'dtgcl',
						type : 'double'
					}, {
						name : 'singlePrice',
						type : 'double'
					}, {
						name : 'taxSinglePrice',
						type : 'double'
					},{
						name : 'singleSumPrice',
						type : 'double'
					},{
						name : 'taxSingleSumPrice',
						type : 'double'
					},{
						name : 'price',
						type : 'double'
					},{
						name : 'sumPrice',
						type : 'double'
					},{
						name : 'parentid',
						type : 'int'
					},{
						name : 'leaf',
						type : 'boolean'
					},{
						name : 'bitProjectId',
						type : 'int'
					},{
						name : 'lookTypeId',
						type : 'int'
					},{
						name : 'seq',
						type : 'int'
					}
		        ]  
		    });  

			var store = Ext.create('Ext.data.TreeStore', {
				model : "User",
				root : {
					  	"name": "整个项目",
					    "singlePrice": 356.85,
					    "taxSinglePrice": 415,
					    "price": 6345.5,
					    "sumPrice": 6345.5,
					    "leaf": false,
					    "expanded": true,
					    "iconCls": "no-icon",
					    "children": [
					        {
					            "bitProjectId": 236,
					            "code": "1-1",
					            "content": 0,
					            "dtgcl": 1,
					            "id": 20,
					            "leaf": true,
					            "lookTypeId": 0,
					            "name": "首层角柱-高度：3010mm  方管柱规格：200mm*200mm*6mm 榫头182*182*5 高度：675mm",
					            "parentid": 0,
					            "price": 1269.1,
					            "remark": "",
					            "seq": 10,
					            "singlePrice": 71.37,
					            "singleSumPrice": 71.37,
					            "sumPrice": 1269.1,
					            "taxSinglePrice": 83,
					            "taxSingleSumPrice": 83,
					            "type": "定",
					            "unit": "个",
					            "iconCls": "no-icon"
					        },
					        {
					            "bitProjectId": 236,
					            "code": "1-1",
					            "content": 0,
					            "dtgcl": 1,
					            "id": 21,
					            "leaf": true,
					            "lookTypeId": 0,
					            "name": "首层角柱-高度：3010mm  方管柱规格：200mm*200mm*6mm 榫头182*182*5 高度：675mm",
					            "parentid": 0,
					            "price": 1269.1,
					            "remark": "",
					            "seq": 10,
					            "singlePrice": 71.37,
					            "singleSumPrice": 71.37,
					            "sumPrice": 1269.1,
					            "taxSinglePrice": 83,
					            "taxSingleSumPrice": 83,
					            "type": "定",
					            "unit": "个",
					            "iconCls": "no-icon"
					        },
					        {
					            "bitProjectId": 236,
					            "code": "1-1",
					            "content": 0,
					            "dtgcl": 1,
					            "id": 22,
					            "leaf": true,
					            "lookTypeId": 0,
					            "name": "首层角柱-高度：3010mm  方管柱规格：200mm*200mm*6mm 榫头182*182*5 高度：675mm",
					            "parentid": 0,
					            "price": 1269.1,
					            "remark": "",
					            "seq": 10,
					            "singlePrice": 71.37,
					            "singleSumPrice": 71.37,
					            "sumPrice": 1269.1,
					            "taxSinglePrice": 83,
					            "taxSingleSumPrice": 83,
					            "type": "定",
					            "unit": "个",
					            "iconCls": "no-icon"
					        },
					        {
					            "bitProjectId": 236,
					            "code": "1-1",
					            "content": 0,
					            "dtgcl": 1,
					            "id": 11,
					            "leaf": true,
					            "lookTypeId": 0,
					            "name": "首层角柱-高度：3010mm  方管柱规格：200mm*200mm*6mm 榫头182*182*5 高度：675mm",
					            "parentid": 0,
					            "price": 1269.1,
					            "remark": "",
					            "seq": 11,
					            "singlePrice": 71.37,
					            "singleSumPrice": 71.37,
					            "sumPrice": 1269.1,
					            "taxSinglePrice": 83,
					            "taxSingleSumPrice": 83,
					            "type": "定",
					            "unit": "个",
					            "iconCls": "no-icon"
					        },
					        {
					            "bitProjectId": 236,
					            "code": "1-1",
					            "content": 0,
					            "dtgcl": 1,
					            "id": 15,
					            "leaf": true,
					            "lookTypeId": 0,
					            "name": "首层角柱-高度：3010mm  方管柱规格：200mm*200mm*6mm 榫头182*182*5 高度：675mm",
					            "parentid": 0,
					            "price": 1269.1,
					            "remark": "",
					            "seq": 12,
					            "singlePrice": 71.37,
					            "singleSumPrice": 71.37,
					            "sumPrice": 1269.1,
					            "taxSinglePrice": 83,
					            "taxSingleSumPrice": 83,
					            "type": "定",
					            "unit": "个",
					            "iconCls": "no-icon"
					        }
					    ]
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
		                dataIndex:"code",  
		            },{  
		                text: "Column B",  
		                dataIndex: "name",  
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