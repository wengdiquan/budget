
//运材安汇总
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.ProjectYCATotalPanel', {
		extend : 'Ext.panel.Panel',
		plain : true,
		border : true,
		width : "100%",
		hieght: "100%",
		autoScroll : true,
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout : 'border',
				items : []
			});
			this.callParent(arguments);
		}
	});
	
});