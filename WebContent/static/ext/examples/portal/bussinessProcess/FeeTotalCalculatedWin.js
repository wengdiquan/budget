Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.FeeTotalCalculatedWin', {
		extend : 'Ext.window.Window',
		title:"查询费用",
		initComponent : function() {
          	var me = this;
  			Ext.apply(this, {
  				width : "70%",
				height : "70%",
				bodyPadding : '5 5',
				modal : true,
				layout : 'fit',
				items : [
					{	
			              region:'center', 
			              id: 'center-region-containeraa', 
			              layout: 'border', 
			              items : [
			            	  			Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeTypeGridWin'),
			            	  			Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeValueGridWin')
			                       ]
			         }
				]
  			});
  			this.callParent(arguments);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeTypeGridWin', {
		extend : 'Ext.tree.Panel',
		id : 'feetotal-feetypegridwin',
		region : 'west',
		width : '18%',
		border : true,
		title:"费用名称",
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			var costTypeStore = Ext.create('Ext.data.TreeStore', {
				autoLoad : true,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/lookvalue/queryLookTypeInfo',
					extraParams : {listCode:"COST_CODE"},
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			Ext.apply(this, {
				rootVisible : false,
				store : costTypeStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.get('id') == 0){
							return;
						}
						
						me.lookTypeId = record.get('id');
						
						Ext.getCmp('feetotal-feevaluegridwin').getStore().load({
							params : {
								'lookTypeId' : me.lookTypeId
							}
						});
					}
				}
			});
			this.callParent(arguments);
		}
	});

	// 详细值
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeValueGridWin', {
		extend : 'Ext.grid.Panel',
		id : 'feetotal-feevaluegridwin',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('TotalYCAList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'id',
					type : 'int'
				}, 'code', 'type', 'name', 'unit', 'tax_Price', 'notax_Price', 'amount', 'tax_Single_SumPrice', 'single_SumPrice'  ]
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'TotalYCAList',
				autoLoad : true,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/ycatotal/queryYCATotalInfo',
					extraParams : {"lookTypeId": Ext.getCmp('feetotal-feetypegridwin').lookTypeId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text:'', width:25,xtype:'rownumberer'
			}
			,{
				text : "编码",
				dataIndex : 'code',
				sortable : false,
				width : '15%'
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '15%'
			},{
				text : "含税合价",
				dataIndex : 'tax_Single_SumPrice',
				sortable : false,
				width : '12%'
			},{
				text : "不含税合价",
				dataIndex : 'single_SumPrice',
				sortable : false,
				width : '12%'
			}];
			Ext.apply(this, {
				store : costValueStore,
				selModel : Ext.create('Ext.selection.Model'),
				columns : costValueColumns,
				bbar : Ext.create('Ext.PagingToolbar', {
					store : costValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
			this.callParent(arguments);
		},
		listeners : {
			'itemdblclick' : function(_this, record, td, cellIndex, tr, rowIndex, e, eOpts){
				var feeTotalId = Ext.getCmp('feetotal-feedetailgrid').feeTotalId;
				if (!feeTotalId) {
					globalObject.infoTip('请先选择一条费用汇总记录！');
					return;
				} else {  
		        	var data = Ext.getCmp('feetotal-feedetailgrid').getSelectionModel().getSelection();  
                    var vals = {
                    		id : feeTotalId,
                    		calculatedRadix : data[0].get("calculatedRadix") + "+" + record.data.code ,
                    		radixRemark : data[0].get("radixRemark") + "+" + record.data.name,
                    		amount : data[0].get("amount") + record.data.tax_Single_SumPrice,
                    		cmd : "edit"
                    };
                    Ext.Ajax.request({
						url : appBaseUri + '/feetotal/updateValue',
						params : vals,
						method : "POST",
						success : function(response) {
							if (response.responseText != '') {
								var res = Ext.JSON.decode(response.responseText);
								if (res.success) {
									globalObject.msgTip('操作成功！');
									//Ext.getCmp('feetotal-feedetailgrid').getStore().reload();
									Ext.getCmp('feetotal-feedetailgrid').getStore().loadPage(1);
								} else {
									globalObject.errTip(res.msg);
								}
							}
						},
						failure : function(response) {
							globalObject.errTip('操作失败！');
						}
					});
		        }  
			}
		},
		newCodeCodeFun: function(){
			var me = this;
			var looktypeId = Ext.getCmp('feetotal-feetypegridwin').lookTypeId;
			if (!looktypeId) {
				globalObject.infoTip('请先选择费用大类！');
				return;
			};
			var win = new App.baseDataManage.CostMoneyManage.InfoWindow({
				hidden : true
			})
			var form = win.down('form').getForm();
			form.findField('lookTypeId').setValue(looktypeId);
			win.show();
		},
		editCodeCodeFun : function() {
			var me = this;
			var looktypeId = Ext.getCmp('feetotal-feetypegridwin').lookTypeId;
			if (!looktypeId) {
				globalObject.infoTip('请先选择费用大类！');
				return;
			};
			
			var grid = Ext.getCmp("feetotal-feevaluegridwin");
			var records = grid.getSelectionModel().getSelection()
			if(records.length != 1){
				globalObject.infoTip('请先需要修改的费用信息！');
				return;
			}
			
			var gridRecord = grid.getStore().findRecord('lookvalueId', records[0].get('lookvalueId'));
			
			var win = new App.baseDataManage.CostMoneyManage.InfoWindow({
				hidden : true
			});
			
			var form = win.down('form').getForm();
			form.findField('lookTypeId').setValue(looktypeId);
			form.findField('cmd').setValue('edit');
			form.findField('lookvalueName').setReadOnly(true);
			form.findField('lookvalueName').setDisabled(true);
			form.findField('lookvalueCode').setReadOnly(true);
			form.findField('lookvalueCode').setDisabled(true);
			form.loadRecord(gridRecord);
			win.show();
		}
	});
});

